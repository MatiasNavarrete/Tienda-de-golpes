package com.example.pedido.service;

import com.example.pedido.dto.EnvioRequestDTO;
import com.example.pedido.dto.PagoRequestDTO;
import com.example.pedido.dto.PedidoDTO;
import com.example.pedido.dto.ProductoResponseDTO;
import com.example.pedido.dto.UsuarioResponseDTO;
import com.example.pedido.model.Pedido;
import com.example.pedido.repository.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public PedidoDTO guardarPedido(PedidoDTO pedidoDTO) {
        log.info("Iniciando orquestación de nuevo pedido para el usuario ID: {}", pedidoDTO.getUsuarioId());
        UsuarioResponseDTO usuario = null;
        try {
            log.debug("Consultando ms-usuarios mediante listar completo en el puerto 8081...");
            UsuarioResponseDTO[] usuarios = webClientBuilder.build().get()
                    .uri("http://localhost:8081/api/usuarios/listar")
                    .retrieve()
                    .bodyToMono(UsuarioResponseDTO[].class)
                    .block();

            if (usuarios != null) {
                for (UsuarioResponseDTO u : usuarios) {
                    if (u.getId().equals(pedidoDTO.getUsuarioId())) {
                        usuario = u;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo conectar o mapear con ms-usuarios: {}. Usando flujo de contingencia.", e.getMessage());
        }
        if (usuario == null) {
            log.warn("Usuario ID {} no encontrado. Asignando usuario genérico para no romper el pedido.", pedidoDTO.getUsuarioId());
            usuario = new UsuarioResponseDTO();
            usuario.setId(pedidoDTO.getUsuarioId());
            usuario.setNombre("Usuario Temporal");
            usuario.setEmail("invitado@tiendadegolpes.com");
        }
        log.info("Flujo de usuario procesado para: {}", usuario.getNombre());

        ProductoResponseDTO producto = null;
        try {
            log.debug("Consultando ms-productos para el producto ID: {}", pedidoDTO.getProductoId());
            producto = webClientBuilder.build().get()
                    .uri("http://localhost:8080/api/v1/productos/{id}", pedidoDTO.getProductoId())
                    .retrieve()
                    .bodyToMono(ProductoResponseDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error de comunicación con ms-productos: {}", e.getMessage());
            throw new RuntimeException("El microservicio de Productos no responde.");
        }

        if (producto == null) {
            throw new RuntimeException("El producto seleccionado no existe.");
        }

        Double precioCalculado = producto.getPrecio() * pedidoDTO.getCantidad();
        Pedido pedido = new Pedido();
        pedido.setProductoId(pedidoDTO.getProductoId());
        pedido.setUsuarioId(pedidoDTO.getUsuarioId());
        pedido.setCantidad(pedidoDTO.getCantidad());
        pedido.setPrecioTotal(precioCalculado);
        Pedido guardado = pedidoRepository.save(pedido);

        PagoRequestDTO pagoRequest = new PagoRequestDTO(guardado.getId(), guardado.getPrecioTotal(), "Tarjeta");
        try {
            webClientBuilder.build().post()
                    .uri("http://localhost:8085/api/pagos/procesar")
                    .body(Mono.just(pagoRequest), PagoRequestDTO.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Pago aprobado para el pedido ID: {}", guardado.getId());
        } catch (Exception e) {
            log.error("Fallo en cadena con el microservicio de Pago: {}", e.getMessage());
        }

        EnvioRequestDTO envioRequest = new EnvioRequestDTO(guardado.getId(), "Despacho destinado a: " + usuario.getEmail());
        try {
            webClientBuilder.build().post()
                    .uri("http://localhost:8084/api/envios/crear")
                    .body(Mono.just(envioRequest), EnvioRequestDTO.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Orden de envío generada dinámicamente.");
        } catch (Exception e) {
            log.error("Fallo en cadena con el microservicio de Envío: {}", e.getMessage());
        }

        PedidoDTO respuestaDTO = new PedidoDTO();
        respuestaDTO.setProductoId(guardado.getProductoId());
        respuestaDTO.setUsuarioId(guardado.getUsuarioId());
        respuestaDTO.setCantidad(guardado.getCantidad());
        respuestaDTO.setPrecioTotal(guardado.getPrecioTotal());

        return respuestaDTO;
    }

    public List<PedidoDTO> obtenerTodos() {
        return pedidoRepository.findAll().stream()
                .map(pedido -> {
                    PedidoDTO dto = new PedidoDTO();
                    dto.setProductoId(pedido.getProductoId());
                    dto.setUsuarioId(pedido.getUsuarioId());
                    dto.setCantidad(pedido.getCantidad());
                    dto.setPrecioTotal(pedido.getPrecioTotal());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}