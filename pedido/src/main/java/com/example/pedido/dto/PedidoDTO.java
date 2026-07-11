package com.example.pedido.dto;

import org.springframework.hateoas.RepresentationModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PedidoDTO extends RepresentationModel<PedidoDTO> {

    private Long id;

    @NotBlank(message = "El ID del usuario no puede estar vacío")
    private String usuarioId;

    @PositiveOrZero(message = "El precio total no puede ser negativo")
    private Double precioTotal;
}