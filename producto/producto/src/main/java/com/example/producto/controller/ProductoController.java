package com.example.producto.controller;

import com.example.producto.dto.ProductoDTO;
import com.example.producto.service.ProductoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Slf4j
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // obtener todos los productos
    @GetMapping
    public List<ProductoDTO> listar() {
        log.info("Petición GET recibida lista para listar todos los productos");
        return productoService.listarTodos();
    }

    // obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición Get recibida para obtener producto con ID: {}", id);
        return productoService.buscarPorId(id)
                .map(producto -> {
                    log.debug("Producto con ID {} encontrado exitosamente", id);
                    return new ResponseEntity<>(producto, HttpStatus.OK);
                })
                .orElseGet(() -> {
                 log.warn("Petición Get fallida: Producto con ID {} no encontrado", id);
                 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    // crear un nuevo producto
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Petición POST recibida para crear un nuevo producto: {}", productoDTO.getNombre());
        ProductoDTO guardado = productoService.guardar(productoDTO);
        log.debug("Producto creado con éxito. ID asignado: {}", guardado.getId());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    // actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Petición PUT recibida para actualizar producto con ID: {}", id);
        return productoService.buscarPorId(id)
                .map(p -> {
                    productoDTO.setId(id);
                    ProductoDTO actualizado = productoService.guardar(productoDTO);
                    log.debug("Producto con ID {} actualizado correctamente", id);
                    return new ResponseEntity<>(actualizado, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Petición PUT fallida: Imposible actualizar, producto ID {} no existe", id);
                    return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    // eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición DELETE recibida para eliminar producto con ID: {}", id);
        if (productoService.buscarPorId(id).isPresent()) {
            productoService.eliminar(id);
            log.debug("Producto con ID {} eliminado del correctamente del sistema", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.warn("Petición DELETE fallida: Producto ID {} no encontrado para eliminar", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}