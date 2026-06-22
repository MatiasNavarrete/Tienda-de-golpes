package com.example.pedido.controller;

import com.example.pedido.dto.PedidoDTO;
import com.example.pedido.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedido Controller", description = "Endpoints para la gestión y creación de pedidos en la tienda")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(
            summary = "Crear un nuevo pedido",
            description = "Registra un nuevo pedido en el sistema, calcula los montos y gatilla las validaciones de stock"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PedidoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o inconsistentes")
    })
    @PostMapping("/crear")
    public PedidoDTO crear(@RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.guardarPedido(pedidoDTO);
    }

    @Operation(
            summary = "Obtener pedido por ID",
            description = "Busca un pedido específico utilizando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado correctamente",
                    content = @Content(schema = @Schema(implementation = PedidoDTO.class))),
            @ApiResponse(responseCode = "404", description = "El pedido con el ID especificado no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) {
        PedidoDTO pedido = pedidoService.obtenerPorId(id);

        if (pedido != null) {
            // Añadimos los links de navegación automática [cite: 134-135]
            pedido.add(linkTo(methodOn(PedidoController.class).obtenerPorId(id)).withSelfRel());
            pedido.add(linkTo(methodOn(PedidoController.class).listar()).withRel("todos"));

            return ResponseEntity.ok(pedido);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Listar todos los pedidos",
            description = "Retorna una lista completa con todos los pedidos registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos recuperada con éxito")
    })
    @GetMapping("/listar")
    public List<PedidoDTO> listar() {
        return pedidoService.obtenerTodos();
    }
}