package com.example.carrito.service;

import com.example.carrito.dto.*;
import com.example.carrito.exception.ProductoNoEncontradoException;
import com.example.carrito.exception.StockInsuficienteException;
import com.example.carrito.model.Carrito;
import com.example.carrito.model.ItemCarrito;
import com.example.carrito.repository.CarritoRepository;
import com.example.carrito.repository.ItemCarritoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarritoService {
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ItemCarritoRepository itemRepository;
    @Autowired private WebClient webClient;

    public CarritoDTO reducirProducto(String usuarioId, Long productoId, int cantidadAReducir) {
       log.info("Solicitud para reducir {} unidades del producto {} en el carrito de: {}", cantidadAReducir, productoId, usuarioId);

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> {
                    log.error("Fallo al reducir: Carrito no encontrado para usuario {}", usuarioId);
                    return new RuntimeException("Carrito no encontrado");
                });

        ItemCarrito item = itemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> {
                    log.warn("Producto {} no existe en el carrito del usuario {}", productoId, usuarioId);
                    return new RuntimeException("Producto no encontrado en el carrito");
                });

        int nuevaCantidad = item.getCantidad() - cantidadAReducir;

        if (nuevaCantidad <= 0) {
            log.debug("La cantidad resultante es {} o menos. Eliminando item {} del carrito", nuevaCantidad, productoId);
            itemRepository.delete(item); // Se elimina si llega a 0 o menos
        } else {
            log.debug("Actualizando cantidad del producto {} de {} a {}", productoId, item.getCantidad(), nuevaCantidad);
            item.setCantidad(nuevaCantidad);
            itemRepository.save(item);
        }

        return obtenerCarrito(usuarioId);
    }

    public CarritoDTO agregarProducto(String usuarioId, ItemRequestDTO request) {
        log.info("Intento de agregar producto {} (cantidad: {}) al carrito de usuario {}",
                request.getProductoId(), request.getCantidad(), usuarioId);

        //obtener o crear el carrito
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    log.info("Carrito no encontrado para usuario {}. Creando carrito nuevo.", usuarioId);
                    return carritoRepository.save(new Carrito(null, usuarioId, null));
                });

        log.debug("Consultando existencia del producto {} en MS-PRODUCTOS", request.getProductoId());
        ProductoResponse producto = webClient.get()
                .uri("http://localhost:8080/api/v1/productos/{id}", request.getProductoId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    log.error("Validación fallida: El producto {} no existe en el catálogo", request.getProductoId());
                    return Mono.error(new ProductoNoEncontradoException("El producto con ID " + request.getProductoId() + " no existe."));
                })
                .bodyToMono(ProductoResponse.class)
                .block();

        log.debug("Consultando stock disponible para producto {} en MS-INVENTARIO", request.getProductoId());
        InventarioResponse inv = webClient.get()
                .uri("http://localhost:9090/api/v1/inventario/{productoId}", request.getProductoId())
                .retrieve()
                .bodyToMono(InventarioResponse.class)
                .block();

        int stockDisponible = (inv != null) ? inv.getStock() : 0;


        //logica para controlar productos en carrito
        itemRepository.findByCarritoIdAndProductoId(carrito.getId(), request.getProductoId())
                .map(itemExistente -> {
                    int cantidadFinal = itemExistente.getCantidad() + request.getCantidad();
                    //valida que la suma no supere el stock
                    if (cantidadFinal > stockDisponible) {
                        log.warn("Stock insuficiente para producto {}. Disponible: {}, Requerido total: {}",
                                request.getProductoId(), stockDisponible, cantidadFinal);
                        throw new StockInsuficienteException(
                                String.format("Stock insuficiente. Ya tienes %d en el carrito, intentas sumar %d, pero el stock total es %d.",
                                        itemExistente.getCantidad(), request.getCantidad(), stockDisponible)
                        );
                    }
                    itemExistente.setCantidad(cantidadFinal);
                    log.info("Actualizando cantidad del producto {} en el carrito: Nueva cantidad {}", request.getProductoId(), cantidadFinal);
                    return itemRepository.save(itemExistente);
                })
                .orElseGet(() -> {
                //si es nuevo, se valida que la cantidad inicial no supere al stock
                if (request.getCantidad() > stockDisponible) {
                    log.warn("Stock insuficiente para nuevo item {}. Disponible: {}, Solicitado: {}",
                            request.getProductoId(), stockDisponible, request.getCantidad());
                    throw new StockInsuficienteException("Stock insuficiente, solo quedan " + stockDisponible + " unidades.");
                }
                log.info("Añadiendo nuevo producto {} al carrito", request.getProductoId());
                ItemCarrito nuevoItem = new ItemCarrito(null, carrito, request.getProductoId(), request.getCantidad());
                return itemRepository.save(nuevoItem);
        });

        return obtenerCarrito(usuarioId);
    }

    public CarritoDTO obtenerCarrito(String usuarioId) {
        log.info("Obteniendo detalles del carrito para usuario: {}", usuarioId);

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    log.debug("Usuario {} no tiene carrito. Retornando vacío.", usuarioId);
                    return carritoRepository.save(new Carrito(null, usuarioId, null ));
                });

        //usamos nuestro repositorio para trar solo los items de este carrito
        List<ItemCarrito> items = itemRepository.findByCarritoId(carrito.getId());

        List<ItemCarritoDTO> itemDto =items.stream().map(item -> {
            log.debug("Recuperando información de catálogo para item ID: {}", item.getProductoId());
            ProductoResponse p = webClient.get()
                    .uri("http://localhost:8080/api/v1/productos/{id}", item.getProductoId())
                    .retrieve()
                    .onStatus(status -> status.isError(), res -> {
                        log.error("Error al consultar producto {} durante el listado del carrito", item.getProductoId());
                        return Mono.empty();
                    })
                    .bodyToMono(ProductoResponse.class)
                    .onErrorReturn(new ProductoResponse())
                    .block();

            return new ItemCarritoDTO(
                    item.getProductoId(),
                    //"p" de producto
                    p != null ? p.getNombre(): "Producto Desconocido",
                    p != null ? p.getPrecio(): 0,
                    item.getCantidad()
            );
        }).collect(Collectors.toList());

        return new CarritoDTO(carrito.getId(), carrito.getUsuarioId(), itemDto);
    }
    public void eliminarProducto(String usuarioId, Long productoId) {
        log.info("Eliminado producto {} del carrito del usuario {}", productoId, usuarioId);
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = itemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> {
                    log.warn("No se puede eliminat: Producto {} no estaba en el carrito", productoId);
                    return new RuntimeException("Producto no encontrado en el carrito");
                        });
        // 3. Eliminamos el item
        itemRepository.delete(item);
        log.debug("Eliminación exitosa de producto {} para usuario {}", productoId, usuarioId);
    }

    public void vaciarCarrito(String usuarioId) {
        log.info("Vaciando completamente el carrito del usuario {}", usuarioId);
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        List<ItemCarrito> items = itemRepository.findByCarritoId(carrito.getId());
        itemRepository.deleteAll(items);
        log.info("Se han eliminado todos los items (total: {}) del carrito del usuario {}", items.size(), usuarioId);
    }
}
