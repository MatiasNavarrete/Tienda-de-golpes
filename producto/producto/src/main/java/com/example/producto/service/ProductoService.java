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

    public ProductoDTO guardar(ProductoDTO dto){

        //convierte DTO a entidad
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());

        //se guarda en BD
        Producto guardado = productoRepository.save(producto);

        //consulta el stock real al servicio de inventario
        Integer stockActual = 0;
        try {
            InventarioResponse inv = webClient.get()
                    .uri("/api/v1/inventario/{id}", guardado.getId())
                    .retrieve()
                    .bodyToMono(InventarioResponse.class)
                    .block();
            if (inv != null) stockActual = inv.getStock();
        } catch (Exception e) {
            System.err.println("No se pudo obtener stock: " + e.getMessage());
        }

        return new ProductoDTO(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getDescripcion(),
                guardado.getPrecio(),
                stockActual

        );
    }

    public void eliminar(Long id){
        productoRepository.deleteById(id);
    }
}