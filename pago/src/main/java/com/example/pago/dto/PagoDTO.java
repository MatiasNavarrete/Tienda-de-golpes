package com.example.pago.dto;

import org.springframework.hateoas.RepresentationModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PagoDTO extends RepresentationModel<PagoDTO> {
    private Long pedidoId;
    private Double monto;
    private String metodoPago;
}