package com.example.busqueda.controller;

import com.example.busqueda.model.Producto;
import com.example.busqueda.service.BusquedaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/busqueda")
@Tag(name = "Búsqueda de Productos", description = "Endpoints para indexar y filtrar el catálogo de la tienda")
public class BusquedaController {

    @Autowired
    private BusquedaService busquedaService;

    @PostMapping("/admin/indexar")
    @Operation(summary = "Indexar un nuevo producto", description = "Guarda un artículo e inyecta enlaces HATEOAS para su localización inmediata")
    public ResponseEntity<EntityModel<Producto>> indexar(@RequestBody Producto producto) {
        Producto guardado = busquedaService.guardarProducto(producto);
        EntityModel<Producto> modelo = EntityModel.of(guardado);

        modelo.add(linkTo(methodOn(BusquedaController.class).buscar("")).withRel("catalogo-completo"));
        return ResponseEntity.ok(modelo);
    }

    @GetMapping("/productos")
    @Operation(summary = "Filtrar productos por nombre", description = "Busca productos que coincidan con el criterio devolviendo hipermedios narrativos")
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> buscar(@RequestParam(required = false, defaultValue = "") String nombre) {
        List<Producto> productos = busquedaService.buscarPorNombre(nombre);

        List<EntityModel<Producto>> productosModelos = productos.stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(BusquedaController.class).buscar(nombre)).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Producto>> coleccion = CollectionModel.of(productosModelos);
        coleccion.add(linkTo(methodOn(BusquedaController.class).buscar(nombre)).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }
}