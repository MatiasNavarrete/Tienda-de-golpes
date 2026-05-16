package com.example.pedido.service;

import com.example.pedido.dto.EnvioRequestDTO;
import com.example.pedido.dto.PagoRequestDTO;
import com.example.pedido.dto.PedidoDTO;
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
        log.info("Iniciando proceso para guardar un nuevo pedido...");

        Pedido pedido = new Pedido();
        pedido.setProductoId(pedidoDTO.getProductoId());
        pedido.setCantidad(pedidoDTO.getCantidad());
        pedido.setPrecioTotal(pedidoDTO.getPrecioTotal());
        Pedido guardado = pedidoRepository.save(pedido);

        log.info("Pedido guardado en BD con ID: {}", guardado.getId());

        PagoRequestDTO pagoRequest = new PagoRequestDTO(guardado.getId(), guardado.getPrecioTotal(), "Tarjeta");
        try {
            log.debug("Intentando contactar al de Pago...");
            webClientBuilder.build().post()
                    .uri("http://localhost:8085/api/pagos/procesar")
                    .body(Mono.just(pagoRequest), PagoRequestDTO.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Pago procesado exitosamente para el pedido: {}", guardado.getId());
        } catch (Exception e) {
            log.error("Error crítico al contactar el Pago para el pedido {}: {}", guardado.getId(), e.getMessage());
        }

        EnvioRequestDTO envioRequest = new EnvioRequestDTO(guardado.getId(), "Dirección de Ejemplo 123");
        try {
            log.debug("Intentando contactar al Envío...");
            webClientBuilder.build().post()
                    .uri("http://localhost:8084/api/envios/crear")
                    .body(Mono.just(envioRequest), EnvioRequestDTO.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Envío solicitado exitosamente para el pedido: {}", guardado.getId());
        } catch (Exception e) {
            log.error("Error crítico al contactar con el Envío para el pedido {}: {}", guardado.getId(), e.getMessage());
        }

        PedidoDTO respuestaDTO = new PedidoDTO();
        respuestaDTO.setProductoId(guardado.getProductoId());
        respuestaDTO.setCantidad(guardado.getCantidad());
        respuestaDTO.setPrecioTotal(guardado.getPrecioTotal());

        log.info("Proceso de guardado de pedido finalizado con éxito.");
        return respuestaDTO;
    }

    public List<PedidoDTO> obtenerTodos() {
        log.info("Consultando la lista de todos los pedidos...");
        return pedidoRepository.findAll().stream()
                .map(pedido -> {
                    PedidoDTO dto = new PedidoDTO();
                    dto.setProductoId(pedido.getProductoId());
                    dto.setCantidad(pedido.getCantidad());
                    dto.setPrecioTotal(pedido.getPrecioTotal());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}