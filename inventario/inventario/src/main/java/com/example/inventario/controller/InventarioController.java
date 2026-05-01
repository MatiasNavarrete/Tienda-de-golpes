package com.example.inventario.controller;

import com.example.inventario.model.Inventario;
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
    public ResponseEntity<Inventario> obtenerPorProductoId(@PathVariable Long productoId) {
        return inventarioService.obtenerPorProductoId(productoId)
                .map(inv -> new ResponseEntity<>(inv, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //listar todo el inventario
    @GetMapping
    public List<Inventario> listarTodos() {
        return inventarioService.listarTodos();
    }

    //guardar nuevo registro de stock
    @PostMapping
    public ResponseEntity<Inventario> crear(@Valid @RequestBody Inventario inventario) {
        return new ResponseEntity<>(inventarioService.guardar(inventario), HttpStatus.CREATED);

    }
}
