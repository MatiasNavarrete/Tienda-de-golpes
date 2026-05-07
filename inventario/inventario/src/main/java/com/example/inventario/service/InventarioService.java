package com.example.inventario.service;

import com.example.inventario.dto.InventarioDTO;
import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    //convertimos entidad a DTO al listar
    public List<InventarioDTO> listarTodos() {
        return inventarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    //convertimos entidad a DTO al buscar
    public Optional<InventarioDTO> obtenerPorProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .map(this::convertirADTO);
    }

    //guardar o actualizar stock
    public InventarioDTO guardar(InventarioDTO dto) {

        //busca si existe un registro para ese productoId
        Inventario inventario = inventarioRepository.findByProductoId(dto.getProductoId())
                .orElse(new Inventario());// si no existe, creamos una instancia nueva de registro

        //le da los valores (si ya existía, sobreescribe el stock)
        inventario.setProductoId(dto.getProductoId());
        inventario.setStock(dto.getStock());

        Inventario guardado = inventarioRepository.save(inventario);

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
        inventarioRepository.deleteById(id);
    }
}