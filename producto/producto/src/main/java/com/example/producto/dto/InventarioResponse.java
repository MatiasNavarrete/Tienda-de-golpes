package com.example.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de sincronización interna que detalla el stock de un producto")
public class InventarioResponse {

        @Schema(description = "Identificador del producto asociado", example = "7")
        private Long productoId;

        @Schema(description = "Existencias actuales del producto en almacén", example = "500")
        private int stock;
}
