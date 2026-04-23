package com.example.producto.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class Producto {

    @NotNull
    private int id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String descripción;
    @NotNull
    private int precio;
    @NotNull
    private int stock;

}
