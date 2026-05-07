package com.example.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventarioDTO {

    private Long id;

    @NotNull(message = "¡El ID del producto es obligatorio!")
    private Long productoId;

    @PositiveOrZero(message = "¡El stock no puede ser negativo!")
    private int stock;

}
