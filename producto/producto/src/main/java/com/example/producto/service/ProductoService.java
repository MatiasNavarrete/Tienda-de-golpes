package com.example.producto.service;

import com.example.producto.dto.InventarioResponse;
import com.example.producto.dto.ProductoDTO;
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

    public Optional<ProductoDTO> buscarPorId(Long id){
        System.out.println(">>> [DEBUG] Buscando producto en BD local con ID: " + id);

        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            System.out.println(">>> [DEBUG] Producto encontrado. Consultando stock en inventario...");

            Integer stock= 0; //valor de stock por defecto
            try {
                InventarioResponse inventario = webClient.get()
                        .uri("/api/v1/inventario/{id}", id)
                        .retrieve()
                        .bodyToMono(InventarioResponse.class)
                        .block();

                if (inventario != null) {
                    stock = inventario.getStock();
                    System.out.println(">>> [DEBUG] Respuesta recibida de Inventario: Stock = " + stock);
                }
            } catch (Exception e) {
                System.err.println(">>> [ERROR] Falló la comunicación con Inventario: " + e.getMessage());
            }

            //construimos el DTO combinando datos de BD y de inventario
            ProductoDTO dto = new ProductoDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    stock
            );

            return Optional.of(dto);
        } else {
            System.out.println(">>> [DEBUG] Producto con ID " + id + " no existe en BD local.");
            return Optional.empty();
        }
    }

    public Producto guardar(Producto producto){
        return productoRepository.save(producto);
    }

    public void eliminar(Long id){
        productoRepository.deleteById(id);
    }
}