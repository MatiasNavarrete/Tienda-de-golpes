package com.example.envio.controller;

import com.example.envio.dto.EnvioDTO;
import com.example.envio.model.Envio;
import com.example.envio.service.EnvioService;
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
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envio Controller", description = "Endpoints para la gestión, despacho y seguimiento logístico de los paquetes")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Operation(summary = "Crear un nuevo despacho de envío", description = "Registra una nueva orden de despacho en el sistema logístico, asignando dirección y datos de entrega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Despacho generado y agendado correctamente", content = @Content(schema = @Schema(implementation = Envio.class))),
            @ApiResponse(responseCode = "400", description = "Datos de envío inválidos, como dirección incompleta o código postal erróneo")
    })
    @PostMapping
    public ResponseEntity<Envio> crear(@Valid @RequestBody EnvioDTO envioDTO) {
        Envio envio = envioService.crearEnvio(envioDTO);
        envio.add(linkTo(methodOn(EnvioController.class).listar()).withRel("todos"));
        envio.add(linkTo(methodOn(EnvioController.class).crear(envioDTO)).withSelfRel());
        return ResponseEntity.status(201).body(envio);
    }

    @Operation(summary = "Listar todos los envíos", description = "Retorna un listado con el estado logístico de todos los despachos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado logístico recuperado con éxito")
    })
    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        return ResponseEntity.ok(envioService.listarTodos());
    }
}