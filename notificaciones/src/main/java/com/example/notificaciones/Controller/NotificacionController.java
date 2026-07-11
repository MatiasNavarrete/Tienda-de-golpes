package com.example.notificaciones.Controller;


import com.example.notificaciones.dto.NotificacionDto;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Endpoints para el procesamiento de alertas del ecosistema")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @PostMapping
    @Operation(summary = "Recibir y procesar una notificación", description = "Recibe el DTO desde otros microservicios, lo guarda en la DB y genera enlaces HATEOAS")
    public ResponseEntity<EntityModel<NotificacionDto>> recibirNotificacion(@RequestBody @Valid NotificacionDto dto) {
        notificacionService.procesarNotificacion(dto);

        EntityModel<NotificacionDto> modelo = EntityModel.of(dto);

        modelo.add(linkTo(methodOn(NotificacionController.class).recibirNotificacion(dto)).withSelfRel());

        return ResponseEntity.ok(modelo);
    }
}