package com.example.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Estructura de datos que representa a un producto comercial de la tienda")
public class ProductoDTO {

    @Schema(description = "Identificador único del producto", example = "7", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "¡El nombre del producto es obligatorio!")
    @Schema(description = "Nombre comercial del artículo", example = "Combo en la guata", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "¡La descripción del producto es obligatoria!")
    @Schema(description = "Descripción detallada del contenido o especificaciones", example = "Un puñetazo directo al abdomen, es posible que quedes sin aire")
    private String descripcion;


    @Positive(message = "¡El precio del producto debe ser mayor a 0!")
    @Schema(description = "Precio unitario asignado al producto", example = "99", requiredMode = Schema.RequiredMode.REQUIRED)
    private int precio;

    @Schema(description = "Cantidad de existencias físicas disponibles recuperadas en tiempo real de ms-inventario", example = "900", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer stock; //campo nuevo que viene del otro servicio inventario
}