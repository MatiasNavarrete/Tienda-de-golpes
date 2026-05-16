package com.example.resena.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "resenas")
@Data
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "no puede estar vacio")
    @Size(min = 10, message = "minimo 10 caracteres")
    private String comentario;

    @Min(1) @Max(5)
    private int estrellas;
}