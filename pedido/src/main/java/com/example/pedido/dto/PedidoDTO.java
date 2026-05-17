package com.example.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {
    private Long productoId;//vinculado al producto
    private Long usuarioId; //vinculado al usuario que hace la compra
    private Integer cantidad;
    private Double precioTotal;
}