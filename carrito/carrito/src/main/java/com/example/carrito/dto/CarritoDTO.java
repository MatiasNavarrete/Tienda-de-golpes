package com.example.carrito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta estructurada que representa el estado completo del carrito de un usuario")
public class CarritoDTO {

    @Schema(
            description = "Identificador único de la entidad Carrito en la base de datos",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "ID del usuario propietario del carrito de compras",
            example = "usuario123"
    )
    private String usuarioId;

    @Schema(description = "Listado detallado de los artículos contenidos actualmente dentro del carrito")
    private List<ItemCarritoDTO> items;

    @Schema(
            description = "Cálculo en tiempo real de la suma de todos los subtotales de la lista de ítems",
            example = "12500",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    public int getTotalGeneral() {
        return items != null ? items.stream().mapToInt(ItemCarritoDTO::getSubtotal).sum() : 0;
    }
}