package com.example.producto.service;

import com.example.producto.dto.InventarioResponse;
import com.example.producto.model.Producto;
import com.example.producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private WebClient webClient;

    public List<Producto> listarTodos(){
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Long id){
        //buscapos el producto en nuestra propia base de datos
        Optional<Producto> producto = productoRepository.findById(id);

        //si existe, llamamos al inventario para obtener el stock
        if (producto.isPresent()) {
            InventarioResponse inventario = webClient.get()
                    .uri("/api/v1/inventario/{id}", id)
                    .retrieve()
                    .bodyToMono(InventarioResponse.class)
                    .block();
            System.out.println("Stock obtendo desde Inventario: "+
                                (inventario != null ? inventario.getStock() : "Sin stock disponible"));

        }

        return producto;
    }

    public Producto guardar(Producto producto){
        return productoRepository.save(producto);
    }

    public void eliminar(Long id){
        productoRepository.deleteById(id);
    }
}