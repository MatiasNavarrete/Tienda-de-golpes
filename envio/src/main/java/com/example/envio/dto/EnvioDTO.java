package com.example.envio.dto;

import org.springframework.hateoas.RepresentationModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnvioDTO extends RepresentationModel<EnvioDTO> {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "La dirección de destino no puede estar vacía")
    private String direccionDestino;
}