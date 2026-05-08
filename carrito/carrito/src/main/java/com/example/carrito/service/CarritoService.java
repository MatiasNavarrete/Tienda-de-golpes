package com.example.carrito.service;

import com.example.carrito.dto.*;
import com.example.carrito.exception.ProductoNoEncontradoException;
import com.example.carrito.exception.StockInsuficienteException;
import com.example.carrito.model.Carrito;
import com.example.carrito.model.ItemCarrito;
import com.example.carrito.repository.CarritoRepository;
import com.example.carrito.repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.SQLOutput;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarritoService {
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ItemCarritoRepository itemRepository;
    @Autowired private WebClient webClient;

    public CarritoDTO reducirProducto(String usuarioId, Long productoId, int cantidadAReducir) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        ItemCarrito item = itemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        int nuevaCantidad = item.getCantidad() - cantidadAReducir;

        if (nuevaCantidad <= 0) {
            itemRepository.delete(item); // Se elimina si llega a 0 o menos
        } else {
            item.setCantidad(nuevaCantidad);
            itemRepository.save(item);
        }

        return obtenerCarrito(usuarioId);
    }

    public CarritoDTO agregarProducto(String usuarioId, ItemRequestDTO request) {
        //obtener o crear el carrito
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(new Carrito(null, usuarioId, null)));

        ProductoResponse producto = webClient.get()
                .uri("http://localhost:8080/api/v1/productos/{id}", request.getProductoId())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        Mono.error(new ProductoNoEncontradoException("El producto con ID " + request.getProductoId() + " no existe.")))
                .bodyToMono(ProductoResponse.class)
                .block();

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
                        throw new StockInsuficienteException(
                                String.format("Stock insuficiente. Ya tienes %d en el carrito, intentas sumar %d, pero el stock total es %d.",
                                        itemExistente.getCantidad(), request.getCantidad(), stockDisponible)
                        );
                    }
                    itemExistente.setCantidad(cantidadFinal);
                    return itemRepository.save(itemExistente);
                })
                .orElseGet(() -> {
                //si es nuevo, se valida que la cantidad inicial no supere al stock
                if (request.getCantidad() > stockDisponible) {
                    throw new StockInsuficienteException("Stock insuficiente, solo quedan " + stockDisponible + " unidades.");
                }
                ItemCarrito nuevoItem = new ItemCarrito(null, carrito, request.getProductoId(), request.getCantidad());
                return itemRepository.save(nuevoItem);
        });

        return obtenerCarrito(usuarioId);
    }

    public CarritoDTO obtenerCarrito(String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(new Carrito(null, usuarioId, null )));

        //usamos nuestro repositorio para trar solo los items de este carrito
        List<ItemCarrito> items = itemRepository.findByCarritoId(carrito.getId());

        List<ItemCarritoDTO> itemDto =items.stream().map(item -> {

            ProductoResponse p = webClient.get()
                    .uri("http://localhost:8080/api/v1/productos/{id}", item.getProductoId())
                    .retrieve()
                    .onStatus(status -> status.isError(), res -> Mono.empty())
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
        // 1. Buscamos el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // 2. Buscamos el item específico dentro de ese carrito
        ItemCarrito item = itemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        // 3. Eliminamos el item
        itemRepository.delete(item);

    }
    public void vaciarCarrito(String usuarioId) {
        // 1. Buscamos el carrito
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        List<ItemCarrito> items = itemRepository.findByCarritoId(carrito.getId());
        itemRepository.deleteAll(items);
    }
}
