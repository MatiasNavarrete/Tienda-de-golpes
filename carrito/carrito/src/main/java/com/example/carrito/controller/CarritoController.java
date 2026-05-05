package com.example.carrito.controller;

import com.example.carrito.dto.CarritoDTO;
import com.example.carrito.dto.ItemRequestDTO;
import com.example.carrito.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    //agregar producto al carrito
    @PostMapping("/{usuarioId}")
    public ResponseEntity<CarritoDTO> agregarProducto(
            @PathVariable String usuarioId,
            @Valid @RequestBody ItemRequestDTO request) {

        CarritoDTO carrito = carritoService.agregarProducto(usuarioId, request);
        return ResponseEntity.ok(carrito);

    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable String usuarioId) {
        CarritoDTO carrito = carritoService.obtenerCarrito(usuarioId);
        return ResponseEntity.ok(carrito);

    }
    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable String usuarioId,
            @PathVariable Long productoId) {

        carritoService.eliminarProducto(usuarioId, productoId);
        return ResponseEntity.noContent().build(); // 204 No Content es lo estándar para eliminar
    }

}
