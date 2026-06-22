package com.example.pago.model;

import org.springframework.hateoas.RepresentationModel;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Pago extends RepresentationModel<Pago> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pedidoId;
    private Double monto;
    private String metodoPago;
    private String estado;
}