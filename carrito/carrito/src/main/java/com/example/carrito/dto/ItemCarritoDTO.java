package com.example.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Detalle de un producto específico contenido dentro del carrito")
public class ItemCarritoDTO extends RepresentationModel<ItemCarritoDTO> {
    private Long productoId;
    private String nombre;
    private int precio;
    private Integer cantidad;

    //para subtotal en carrito
    public int getSubtotal() {
        return this.precio * this.cantidad;
    }
}