package com.example.producto.service;

import com.example.producto.model.Producto;
import com.example.producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    //obtener todos los productos
    public List<Producto> listarTodos(){
        return productoRepository.findAll();
    }

    //buscar producto por Id
    public Optional<Producto> buscarPorId(Long id){
        return productoRepository.findById(id);
    }

    //guardar producto
    public Producto guardar(Producto producto){
        return productoRepository.save(producto);
    }

    //eliminar
    public void eliminar(Long id){
        productoRepository.deleteById(id);
    }

}
