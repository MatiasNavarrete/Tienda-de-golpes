package com.example.inventario.service;

import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    //listar todo el inventario
    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    //buscar stock por ID de producto (para la futura comunicación)
    public Optional<Inventario> obtenerPorProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    //guardar o actualizar stock
    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    //eliminar stock
    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }
}