package com.example.producto.service;

import com.example.producto.dto.InventarioResponse;
import com.example.producto.dto.ProductoDTO;
import com.example.producto.model.Producto;
import com.example.producto.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private WebClient webClient;

    public List<ProductoDTO> listarTodos() {

        log.info("Solicitando listado completo de productos");
        //traemos todos los productos de la BD
        List<Producto> productos = productoRepository.findAll();
        //convertimos la lista de Producto a ProductoDTO inyectando el stock
        return productos.stream().map(producto -> {
            Integer stock = 0;
            try {
                log.debug("Consultando stock para producto ID: {}", producto.getId());

                InventarioResponse inventario = webClient.get()
                        .uri("/api/v1/inventario/{id}", producto.getId())
                        .retrieve()
                        .bodyToMono(InventarioResponse.class)
                        .block(); //bloqueo necesario para completar el DTO antes de enviarlo

                if (inventario != null) {
                    stock = inventario.getStock();
                }
            } catch (Exception e) {
                //log para exceptiones
                log.error("No se pudo obtener el stock para el producto {}: {}", producto.getId(), e.getMessage());
            }
            //devolvemos el DTO con toda la info
            return new ProductoDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    stock
            );
        }).collect(Collectors.toList());
    }

    public Optional<ProductoDTO> buscarPorId(Long id){
        log.info("Buscando producto con ID: {}", id);

        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            log.debug("Producto {} encontrado en BD. Consultando inventario...", id);

            Integer stock= 0; //valor de stock por defecto
            try {
                InventarioResponse inventario = webClient.get()
                        .uri("/api/v1/inventario/{id}", id)
                        .retrieve()
                        .bodyToMono(InventarioResponse.class)
                        .block();

                if (inventario != null) {
                    stock = inventario.getStock();
                    log.debug("Stock obtenido para ID {}: {}", id, stock);
                }
            } catch (Exception e) {
                log.error("Falló la comunicación con el microservicio de Inventario para ID {}:", id, e.getMessage());
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
            log.warn("Producto con ID {} no fue localizado en la base de datos", id);
            return Optional.empty();
        }
    }

    public ProductoDTO guardar(ProductoDTO dto){
        log.info("Guardando/Actualizando producto: {}", dto.getNombre());
        //convierte DTO a entidad
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());

        //se guarda en BD
        Producto guardado = productoRepository.save(producto);
        log.debug("Producto guardado con éxito. ID generado: {}", guardado.getId());
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
            log.error("Error al recuperar el stock tras ser guardado para ID {}: {}", guardado.getId(), e.getMessage());
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
        log.info("Eliminando producto con ID: {}", id);
        productoRepository.deleteById(id);
        log.debug("Producto con ID: {} eliminado correctamente", id);
    }
}