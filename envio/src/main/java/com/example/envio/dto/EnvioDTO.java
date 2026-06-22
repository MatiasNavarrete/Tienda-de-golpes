package com.example.envio.dto;

import org.springframework.hateoas.RepresentationModel;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) 
public class EnvioDTO extends RepresentationModel<EnvioDTO> {
    private Long pedidoId;
    private String direccionDestino;
}