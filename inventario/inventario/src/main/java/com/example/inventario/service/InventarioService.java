package com.example.inventario.service;

import com.example.inventario.dto.InventarioDTO;
import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    //convertimos entidad a DTO al listar
    public List<InventarioDTO> listarTodos() {
        log.info("Consultando la lista completa de inventarios");
        return inventarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<InventarioDTO> obtenerPorProductoId(Long productoId) {
        log.info("Consultando stock para el producto ID: {}", productoId);
        return inventarioRepository.findByProductoId(productoId)
                .map(inv -> {
                    log.debug("Stock encontrado para producto {}: {} unidades", productoId, inv.getStock());
                    return convertirADTO(inv);
                });
    }

    //guardar o actualizar stock
    public InventarioDTO guardar(InventarioDTO dto) {
        log.info("Solicitud para actualizar/crear stock para producto ID: {}", dto.getProductoId());
        //busca si existe un registro para ese productoId
        Inventario inventario = inventarioRepository.findByProductoId(dto.getProductoId())
                .orElseGet(() -> {
                    log.debug("No se encontró un registro previo para producto {}. Creando uno nuevo.", dto.getProductoId());
                    return new  Inventario();
                });// si no existe, creamos una instancia nueva de registro

        int stockAnterior = (inventario.getId() !=null) ? inventario.getStock() : 0;
        //le da los valores (si ya existía, sobreescribe el stock)
        inventario.setProductoId(dto.getProductoId());
        inventario.setStock(dto.getStock());

        Inventario guardado = inventarioRepository.save(inventario);

        log.info("Stock actualizado exitosamente para producto {}. Antes: {} | Ahora: {}",
                dto.getProductoId(), stockAnterior, guardado.getStock());
        return convertirADTO(guardado);
    }

    private InventarioDTO convertirADTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(inv.getId());
        dto.setProductoId(inv.getProductoId());
        dto.setStock(inv.getStock());
        return dto;
    }

    //eliminar stock
    public void eliminar(Long id) {
        log.warn("Se eliminará el registro de inventario con ID: {}", id);
        try {
            inventarioRepository.deleteById(id);
            log.info("Registro de inventario ID {} elimindo correctamente", id);
        } catch (Exception e) {
            log.error("Error al intentar eliminar el registro de inventario ID {}: {}", id, e.getMessage());
        }
    }
}