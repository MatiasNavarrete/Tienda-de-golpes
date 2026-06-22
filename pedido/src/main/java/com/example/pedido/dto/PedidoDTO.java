package com.example.pedido.dto;

import org.springframework.hateoas.RepresentationModel;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PedidoDTO extends RepresentationModel<PedidoDTO> {

    private Long id;
    private String usuarioId;
    private Double precioTotal;
}