package com.example.inventario.repository;

import com.example.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    //método para buscar el stock mediante el ID del producto.
    Optional<Inventario> findByProductoId(Long productoId);

}
