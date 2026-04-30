package com.example.Busqueda.contoller;

import com.example.Busqueda.model.Producto;
import com.example.Busqueda.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService){
        this.productoService= productoService;
    }

    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam String nombre){
        return productoService.buscarPorNombre(nombre);
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto){
        return productoService.guardarProducto(producto);
    }

}
