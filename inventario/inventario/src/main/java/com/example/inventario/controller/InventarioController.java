package com.example.inventario.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.example.inventario.dto.InventarioDTO;
import com.example.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventario")
@Slf4j
@Tag(name = "Inventario Controller", description = "Endpoints para el control y supervisión del stock de productos") // Adaptado de la guía
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Obtener stock por ID de producto", description = "Consulta las unidades físicas disponibles de un artículo mediante su ID de producto. Consumido internamente por ms-productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock consultado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no registrado en el inventario")
    })
    @GetMapping("/{productoId}")
    public ResponseEntity<InventarioDTO> obtenerPorProductoId(@PathVariable Long productoId) {
        log.info("Petición GET recibida para consultar stock del producto ID: {}", productoId);
        return inventarioService.obtenerPorProductoId(productoId)
                .map(dto -> {

                    dto.add(linkTo(methodOn(InventarioController.class).obtenerPorProductoId(dto.getProductoId())).withSelfRel());
                    dto.add(linkTo(methodOn(InventarioController.class).listarTodos()).withRel("todos"));

                    log.debug("Respuesta exitosa: Stock disponible para producto {}: {}", productoId, dto.getStock());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("Consulta de stock fallida: El producto ID {} no existe en inventario", productoId);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Listar todo el inventario", description = "Recupera un listado global con el stock de todos los productos mapeados en almacén.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de inventario obtenido con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public List<InventarioDTO> listarTodos() {
        log.info("Petición GET recibida para listar todo el inventario");
        return inventarioService.listarTodos().stream()
                .map(dto -> {
                    dto.add(linkTo(methodOn(InventarioController.class).obtenerPorProductoId(dto.getProductoId())).withSelfRel());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "Guardar o actualizar registro de stock", description = "Crea un nuevo registro de almacén o actualiza las existencias disponibles para un producto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro de stock creado o modificado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. stock negativo)"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioDTO inventarioDTO) {
        log.info("Petición POST recibida para actualizar/crear stock del producto ID: {}", inventarioDTO.getProductoId());
        InventarioDTO guardado = inventarioService.guardar(inventarioDTO);

        guardado.add(linkTo(methodOn(InventarioController.class).obtenerPorProductoId(guardado.getProductoId())).withSelfRel());
        guardado.add(linkTo(methodOn(InventarioController.class).listarTodos()).withRel("todos"));

        log.debug("Stock registrado correctamente para ID: {}", guardado.getProductoId());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }
}
