package com.example.carrito.controller;

import com.example.carrito.dto.CarritoDTO;
import com.example.carrito.dto.ItemRequestDTO;
import com.example.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Carrito Controller", description = "Endpoints para gestionar los carritos de los usuarios")
@SecurityRequirement(name = "bearerAuth")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Reducir cantidad de un producto", description = "Disminuye las unidades de un artículo específico dentro del carrito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad reducida correctamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida solicitada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado en el carrito")
    })
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

    @Operation(summary = "Agregar producto al carrito", description = "Añade un producto y su cantidad al carrito de un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto o usuario no encontrado")
    })
    @PostMapping("/{usuarioId}")
    public ResponseEntity<CarritoDTO> agregarProducto(
            @PathVariable String usuarioId,
            @Valid @RequestBody ItemRequestDTO request) {

        log.info("Petición POST recibida: Agregar producto {} al carrito del usuario {}", request.getProductoId(), usuarioId);
        CarritoDTO carrito = carritoService.agregarProducto(usuarioId, request);
        log.debug("Producto {} agregado correctamente al carrito de {}", request.getProductoId(), usuarioId);
        return ResponseEntity.ok(carrito);
    }

    @Operation(summary = "Obtener carrito del usuario", description = "Recupera los detalles y los artículos actuales del carrito de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable String usuarioId) {
        log.info("Petición GET recibida: Consultar carrito del usuario {}", usuarioId);
        CarritoDTO carrito = carritoService.obtenerCarrito(usuarioId);
        return ResponseEntity.ok(carrito);

    }

    @Operation(summary = "Eliminar un producto por completo", description = "Remueve un artículo del carrito sin importar la cantidad guardada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito del carrito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto o carrito no encontrado")
    })
    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable String usuarioId,
            @PathVariable Long productoId) {

        log.info("Petición DELETE recibida: Eliminar producto {} del carrito del usuario {}", productoId, usuarioId);
        carritoService.eliminarProducto(usuarioId, productoId);
        log.debug("Elimincación confirmada para producto {} en carrito {}", productoId, usuarioId);
        return ResponseEntity.noContent().build(); // 204 No Content es lo estándar para eliminar
    }

    @Operation(summary = "Vaciar todo el carrito", description = "Limpia de forma absoluta todos los ítems almacenados en el carrito del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrito vaciado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado") // [cite: 79]
    })
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable String usuarioId) {
        log.warn("Petición DELETE recibida: VACIAR TODO el carrito del usuario {}", usuarioId); //WARN por ser acción importante grande
        carritoService.vaciarCarrito(usuarioId);
        log.info("Carrito del usuario {} vaciado exitosamente", usuarioId);
        return ResponseEntity.noContent().build();
    }

}
