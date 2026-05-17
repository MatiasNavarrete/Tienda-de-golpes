package com.example.pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionRequestDTO {
    private String destinatario;
    private String asunto;
    private String mensaje;
}