package com.example.resena.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaDTO {

    @NotNull(message = "El ID es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "no puede estar vacio")
    @Size(min = 10, message = "minimo 10 caracteres")
    private String comentario;

    @Min(1) @Max(5)
    private int estrellas;
}