package com.example.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoDTO {
    private Long id;
    private String usuarioId;
    private List<ItemCarritoDTO> items;

}