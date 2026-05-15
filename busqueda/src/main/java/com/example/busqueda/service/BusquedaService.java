package com.example.busqueda.service;

import com.example.busqueda.model.Producto;
import com.example.busqueda.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class BusquedaService {

    @Autowired
    private ProductoRepository repository;

    public List<Producto> buscarPorNombre(String nombre) {
        log.info("Iniciando búsqueda de productos con término: '{}'", nombre);
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    public Producto guardarProducto(Producto producto) {
        log.info("Indexando nuevo producto en el catálogo: {}", producto.getNombre());
        return repository.save(producto);
    }
}