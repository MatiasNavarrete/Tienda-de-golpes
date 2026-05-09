package com.example.inventario.controller;

import com.example.inventario.dto.InventarioDTO;
import com.example.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Slf4j
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    //obtener stock por ID de producto (comunicación con WebClient)
    @GetMapping("/{productoId}")
    public ResponseEntity<InventarioDTO> obtenerPorProductoId(@PathVariable Long productoId) {
        log.info("Petición GET recibida para consultar stock del producto ID: {}", productoId);
        return inventarioService.obtenerPorProductoId(productoId)
                .map(dto -> {
                    log.debug("Respuesta exitosa: Stock disponible para producto {}: {}", productoId, dto.getStock());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("Consulta de stock fallida: El producto ID {} no existe en inventario", productoId);
                    return ResponseEntity.notFound().build();
                });
    }
    //listar todo el inventario
    @GetMapping
    public List<InventarioDTO> listarTodos() {
        log.info("Petición GET recibida para listar todo el inventario");
        return inventarioService.listarTodos();
    }

    //guardar nuevo registro de stock
    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioDTO inventarioDTO) {
        log.info("Petición POST recibida para actualizar/crear stock del producto ID: {}", inventarioDTO.getProductoId());
        InventarioDTO guardado = inventarioService.guardar(inventarioDTO);
        log.debug("Stock registrado correctamente para ID: {}", guardado.getProductoId());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }
}
