package com.example.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Modelo de petición para agregar o modificar artículos en el carrito")
public class ItemRequestDTO {
    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(
            description = "Identificador único del producto en el catálogo general",
            example = "7",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(
            description = "Cantidad de unidades del producto que se desean incorporar al carrito",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer cantidad;

}