package com.example.pedido.service;

import com.example.pedido.dto.*;
import com.example.pedido.model.Pedido;
import com.example.pedido.repository.PedidoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PedidoDTO guardarPedido(PedidoDTO pedidoDTO) {
        log.info("Iniciando procesamiento de compra del carrito para el usuario: {}", pedidoDTO.getUsuarioId());

        CarritoResponseDTO carrito;
        try {
            carrito = webClientBuilder.build()
                    .get()
                    .uri("lb://ms-carrito/api/v1/carrito/{usuarioId}", pedidoDTO.getUsuarioId())
                    .retrieve()
                    .bodyToMono(CarritoResponseDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al conectar con ms-carrito: {}", e.getMessage());
            throw new RuntimeException("El microservicio de carrito no responde.");
        }

        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito del usuario está vacío. No hay productos para comprar.");
        }

        log.info("Carrito recuperado con {} productos diferentes.", carrito.getItems().size());

        double granTotalAcumulado = 0.0;

        // Verificar stock y calcular total
        for (ItemCarritoDTO item : carrito.getItems()) {
            try {
                InventarioResponseDTO inv = webClientBuilder.build()
                        .get()
                        .uri("lb://ms-inventario/api/v1/inventario/{productoId}", item.getProductoId())
                        .retrieve()
                        .bodyToMono(InventarioResponseDTO.class)
                        .block();

                if (inv == null || inv.getStock() < item.getCantidad()) {
                    throw new RuntimeException(
                            "Stock insuficiente para el producto: " + item.getNombre() +
                                    ". Quedan solo: " + (inv != null ? inv.getStock() : 0)
                    );
                }

                granTotalAcumulado += item.getPrecio() * item.getCantidad();

            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                log.error("Error al verificar inventario para producto ID {}: {}", item.getProductoId(), e.getMessage());
                throw new RuntimeException("Error de red con ms-inventario.");
            }
        }

        // Descontar stock
        for (ItemCarritoDTO item : carrito.getItems()) {
            try {
                InventarioResponseDTO inv = webClientBuilder.build()
                        .get()
                        .uri("lb://ms-inventario/api/v1/inventario/{productoId}", item.getProductoId())
                        .retrieve()
                        .bodyToMono(InventarioResponseDTO.class)
                        .block();

                if (inv != null) {
                    int nuevoStock = inv.getStock() - item.getCantidad();
                    InventarioResponseDTO actualizacion =
                            new InventarioResponseDTO(null, item.getProductoId(), nuevoStock);

                    webClientBuilder.build()
                            .post()
                            .uri("lb://ms-inventario/api/v1/inventario")
                            .body(Mono.just(actualizacion), InventarioResponseDTO.class)
                            .retrieve()
                            .bodyToMono(InventarioResponseDTO.class)
                            .block();
                }

            } catch (Exception e) {
                log.error("Fallo al descontar stock físico: {}", e.getMessage());
            }
        }

        // Guardar pedido
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(pedidoDTO.getUsuarioId());
        pedido.setPrecioTotal(granTotalAcumulado);

        Pedido guardado = pedidoRepository.save(pedido);

        // Guardar detalle del pedido
        for (ItemCarritoDTO item : carrito.getItems()) {
            jdbcTemplate.update(
                    "INSERT INTO pedido_detalles (pedido_id, producto_id, cantidad) VALUES (?, ?, ?)",
                    guardado.getId(),
                    item.getProductoId(),
                    item.getCantidad()
            );
        }

        log.info("Pedido guardado con éxito. ID Pedido: {}. Total: ${}", guardado.getId(), granTotalAcumulado);

        // Vaciar carrito
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("lb://ms-carrito/api/v1/carrito/{usuarioId}", pedidoDTO.getUsuarioId())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Carrito del usuario {} vaciado de forma exitosa tras la compra.", pedidoDTO.getUsuarioId());
        } catch (Exception e) {
            log.error("No se pudo vaciar el carrito en ms-carrito: {}", e.getMessage());
        }

        // Pago
        PagoRequestDTO pagoRequest = new PagoRequestDTO(
                guardado.getId(),
                guardado.getPrecioTotal(),
                "Tarjeta"
        );

        try {
            webClientBuilder.build()
                    .post()
                    .uri("lb://ms-pagos/api/pagos/procesar")
                    .body(Mono.just(pagoRequest), PagoRequestDTO.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Pago aprobado para el pedido ID: {}", guardado.getId());
        } catch (Exception e) {
            log.error("Fallo con pagos: {}", e.getMessage());
        }

        // Envío
        EnvioRequestDTO envioRequest = new EnvioRequestDTO(
                guardado.getId(),
                "Despacho procesado para el usuario: " + pedidoDTO.getUsuarioId()
        );

        try {
            webClientBuilder.build()
                    .post()
                    .uri("lb://ms-envios/api/envios/crear")
                    .body(Mono.just(envioRequest), EnvioRequestDTO.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Orden de envío generada dinámicamente.");
        } catch (Exception e) {
            log.error("Fallo con ms-envios: {}", e.getMessage());
        }

        // Notificación
        String mensajeAlerta = "Hola, tu compra por un total de $" + guardado.getPrecioTotal() +
                " ha sido procesada con éxito. ID del pedido: " + guardado.getId();

        NotificacionRequestDTO notificacionRequest = new NotificacionRequestDTO(
                pedidoDTO.getUsuarioId(),
                "Confirmación de Compra - Tienda de Golpes",
                mensajeAlerta
        );

        try {
            webClientBuilder.build()
                    .post()
                    .uri("lb://ms-notificaciones/api/notificaciones")
                    .body(Mono.just(notificacionRequest), NotificacionRequestDTO.class)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Alerta de compra enviada con éxito a Notificaciones.");
        } catch (Exception e) {
            log.error("No se pudo conectar con notificaciones: {}", e.getMessage());
        }

        PedidoDTO respuesta = new PedidoDTO();
        respuesta.setUsuarioId(guardado.getUsuarioId());
        respuesta.setPrecioTotal(guardado.getPrecioTotal());

        return respuesta;
    }

    public List<PedidoDTO> obtenerTodos() {
        return pedidoRepository.findAll().stream()
                .map(p -> new PedidoDTO(p.getId(), p.getUsuarioId(), p.getPrecioTotal()))
                .collect(Collectors.toList());
    }

    public PedidoDTO obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(p -> new PedidoDTO(p.getId(), p.getUsuarioId(), p.getPrecioTotal()))
                .orElse(null);
    }
}