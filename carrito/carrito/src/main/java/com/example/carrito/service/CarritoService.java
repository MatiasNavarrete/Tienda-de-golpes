package com.example.carrito.service;

import com.example.carrito.dto.CarritoDTO;
import com.example.carrito.dto.ItemCarritoDTO;
import com.example.carrito.dto.ItemRequestDTO;
import com.example.carrito.dto.ProductoResponse;
import com.example.carrito.model.Carrito;
import com.example.carrito.model.ItemCarrito;
import com.example.carrito.repository.CarritoRepository;
import com.example.carrito.repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarritoService {
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ItemCarritoRepository itemRepository;
    @Autowired private WebClient webClient;

    public CarritoDTO agregarProducto(String usuarioId, ItemRequestDTO request) {
        //obtener o crear el carrito
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> carritoRepository.save(new Carrito(null, usuarioId, null)));

        //validar que el producto existe en el otro servicio
        ProductoResponse producto = webClient.get()
                .uri("/api/v1/productos/{id}", request.getProductoId())
                .retrieve()
                .bodyToMono(ProductoResponse.class)
                .block();

        if (producto == null) throw new RuntimeException("Producto no encontrado");

        //logica para controlar productos en carrito
        ItemCarrito item = itemRepository.findByCarritoIdAndProductoId(carrito.getId(), request.getProductoId())
                .map(i -> {
                    //si existe, se actualiza la cantidad
                    // "i" hace referencia a item existente, esta es una variable para métodos.
                    i.setCantidad(i.getCantidad() + request.getCantidad());
                    return itemRepository.save(i);
                })
                .orElseGet(() -> {
                    //si no existe, creamos uno nuevo
                    ItemCarrito nuevoItem = new ItemCarrito(null, carrito, request. getProductoId(), request.getCantidad());
                    return itemRepository.save(nuevoItem);
                });

        return obtenerCarrito(usuarioId);
    }

    public CarritoDTO obtenerCarrito(String usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        //usamos nuestro repositorio para trar solo los items de este carrito
        List<ItemCarrito> items = itemRepository.findByCarritoId(carrito.getId());

        List<ItemCarritoDTO> itemDto =items.stream().map(item -> {

            ProductoResponse p = webClient.get()
                    .uri("/api/v1/productos/{id}", item.getProductoId())
                    .retrieve()
                    .bodyToMono(ProductoResponse.class)
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
}
