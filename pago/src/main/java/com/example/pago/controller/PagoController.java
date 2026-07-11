package com.example.pago.controller;

import com.example.pago.dto.PagoDTO;
import com.example.pago.model.Pago;
import com.example.pago.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pago Controller", description = "Endpoints para el procesamiento financiero y consulta de historial de transacciones")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Operation(summary = "Procesar un nuevo pago", description = "Valida los datos de la transacción financiera, efectúa el cobro y actualiza el estado de la orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago procesado y aprobado con éxito", content = @Content(schema = @Schema(implementation = Pago.class))),
            @ApiResponse(responseCode = "400", description = "Inconsistencia en los datos de la tarjeta o fondos insuficientes"),
            @ApiResponse(responseCode = "500", description = "Error de comunicación con la pasarela de pago externa")
    })
    @PostMapping
    public Pago pagar(@Valid @RequestBody PagoDTO pagoDTO) {
        Pago pago = pagoService.procesarPago(pagoDTO);
        pago.add(linkTo(methodOn(PagoController.class).listar()).withRel("historial"));
        pago.add(linkTo(methodOn(PagoController.class).pagar(pagoDTO)).withSelfRel());
        return pago;
    }

    @Operation(summary = "Obtener historial de pagos", description = "Retorna una lista completa de todas las transacciones y cobros registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de pagos recuperado exitosamente")
    })
    @GetMapping
    public List<Pago> listar() {
        return pagoService.listarPagos();
    }
}