package com.example.usuarios.dto;

import lombok.Data;

@Data
public class NotificacionDto {
    private String destinatario;
    private String asunto;
    private String mensaje;
}