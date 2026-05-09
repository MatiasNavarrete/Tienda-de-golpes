package com.example.carrito.controller;

import com.example.carrito.dto.CarritoDTO;
import com.example.carrito.dto.ItemRequestDTO;
import com.example.carrito.service.CarritoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carrito")
@Validated
@Slf4j
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PatchMapping("/{usuarioId}/producto/{productoId}/reducir")
    public ResponseEntity<CarritoDTO> reducirProducto(
            @PathVariable String usuarioId,
            @PathVariable Long productoId,
            @RequestParam @Min(value = 1, message = "La cantidad a reducir debe ser al menos 1") int cantidad) { // Pasamos la cantidad por parámetro

        log.info("Petición PATCH recibida: Reducir {} unidades del producto {} para usuario {}", cantidad, productoId, usuarioId);
        CarritoDTO carrito = carritoService.reducirProducto(usuarioId, productoId, cantidad);
        log.debug("Reducción completada con éxito para usuario {}", usuarioId);
        return ResponseEntity.ok(carrito);
    }

    //agregar producto al carrito
    @PostMapping("/{usuarioId}")
    public ResponseEntity<CarritoDTO> agregarProducto(
            @PathVariable String usuarioId,
            @Valid @RequestBody ItemRequestDTO request) {

        log.info("Petición POST recibida: Agregar producto {} al carrito del usuario {}", request.getProductoId(), usuarioId);
        CarritoDTO carrito = carritoService.agregarProducto(usuarioId, request);
        log.debug("Producto {} agregado correctamente al carrito de {}", request.getProductoId(), usuarioId);
        return ResponseEntity.ok(carrito);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable String usuarioId) {
        log.info("Petición GET recibida: Consultar carrito del usuario {}", usuarioId);
        CarritoDTO carrito = carritoService.obtenerCarrito(usuarioId);
        return ResponseEntity.ok(carrito);

    }
    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable String usuarioId,
            @PathVariable Long productoId) {

        log.info("Petición DELETE recibida: Eliminar producto {} del carrito del usuario {}", productoId, usuarioId);
        carritoService.eliminarProducto(usuarioId, productoId);
        log.debug("Elimincación confirmada para producto {} en carrito {}", productoId, usuarioId);
        return ResponseEntity.noContent().build(); // 204 No Content es lo estándar para eliminar
    }
    //vaciar TODO el carrito del usuario
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable String usuarioId) {
        log.warn("Petición DELETE recibida: VACIAR TODO el carrito del usuario {}", usuarioId); //WARN por ser acción importante grande
        carritoService.vaciarCarrito(usuarioId);
        log.info("Carrito del usuario {} vaciado exitosamente", usuarioId);
        return ResponseEntity.noContent().build();
    }

}
