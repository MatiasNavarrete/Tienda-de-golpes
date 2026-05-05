package com.example.carrito.repository;

import com.example.carrito.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    //buscar items de un carrito específico
    List<ItemCarrito> findByCarritoId(Long carritoId);

    //buscar un producto específico dentro de un carrito
    Optional<ItemCarrito> findByCarritoIdAndProductoId(Long carritoId, Long productoId);

}