package com.example.producto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min= 3, max= 50, message = "El nombre del producto debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción del producto no debe estar vacía")
    private String descripcion;

    @Positive(message = "El precio debe ser mayor a cero")
    private int precio;

    @Min(value = 0, message = "El stock no puede se menor a cero")
    private Integer stock;

}
