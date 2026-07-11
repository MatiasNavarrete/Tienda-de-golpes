package com.example.notificaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificacionDto {

    @NotBlank(message = "El destinatario no puede estar vacío")
    @Email(message = "El formato del destinatario debe ser un correo válido")
    private String destinatario;

    private String asunto;

    @NotBlank(message = "El mensaje no puede estar en blanco")
    private String mensaje;
}