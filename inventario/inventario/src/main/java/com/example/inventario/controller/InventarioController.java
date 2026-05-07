package com.example.inventario.controller;

import com.example.inventario.dto.InventarioDTO;
import com.example.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    //obtener stock por ID de producto (comunicación con WebClient)
    @GetMapping("/{productoId}")
    public ResponseEntity<InventarioDTO> obtenerPorProductoId(@PathVariable Long productoId) {
        return inventarioService.obtenerPorProductoId(productoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //listar todo el inventario
    @GetMapping
    public List<InventarioDTO> listarTodos() {
        return inventarioService.listarTodos();
    }

    //guardar nuevo registro de stock
    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioDTO inventarioDTO) {
        return new ResponseEntity<>(inventarioService.guardar(inventarioDTO), HttpStatus.CREATED);

    }
}
