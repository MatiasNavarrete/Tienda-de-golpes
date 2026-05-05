package com.example.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarritoDTO {
    private Long productoId;
    private String nombre;
    private int precio;
    private Integer cantidad;

    //para subtotal en carrito
    public int getSubtotal() {
        return this.precio * this.cantidad;
    }
}