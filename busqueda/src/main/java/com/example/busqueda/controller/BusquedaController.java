package com.example.busqueda.controller;

import com.example.busqueda.model.Producto;
import com.example.busqueda.service.BusquedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/busqueda")
public class BusquedaController {

    @Autowired
    private BusquedaService service;

    @GetMapping("/productos")
    public List<Producto> buscar(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @PostMapping("/admin/indexar")
    public Producto indexar(@RequestBody Producto producto) {
        return service.guardarProducto(producto);
    }
}