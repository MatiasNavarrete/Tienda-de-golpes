package com.example.producto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private Long id;

    @NotBlank(message = "¡El nombre del producto es obligatorio!")
    private String nombre;

    @NotBlank(message = "¡La descripción del producto es obligatoria!")
    private String descripcion;

    @Positive(message = "¡El precio del producto debe ser mayor a 0!")
    private int precio;

    private Integer stock; //campo nuevo que viene del otro servicio inventario
}