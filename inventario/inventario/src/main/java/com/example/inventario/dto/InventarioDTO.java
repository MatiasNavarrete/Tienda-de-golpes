package com.example.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(description = "Estructura de datos que representa el control de existencias de un producto en almacén")
public class InventarioDTO {

    @Schema(description = "Identificador único del registro de inventario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "¡El ID del producto es obligatorio!")
    @Schema(description = "Identificador del producto vinculado", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @PositiveOrZero(message = "¡El stock no puede ser negativo!")
    @Schema(description = "Cantidad de existencias físicas disponibles en bodega", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
    private int stock;

}
