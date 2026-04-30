package com.example.Notificaciones.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {

    private String destinatario;
    private String asunto;
    private String mensaje;

}
