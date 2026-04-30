package com.example.Busqueda.service;

import com.example.Busqueda.model.Producto;
import com.example.Busqueda.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository){
        this.productoRepository= productoRepository;
    }

    public List<Producto> buscarPorNombre(String nombre){
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Producto guardarProducto(Producto producto){
        return productoRepository.save(producto);
    }

}
