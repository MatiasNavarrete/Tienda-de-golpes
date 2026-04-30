package com.example.Notificaciones.controller;

import com.example.Notificaciones.model.Notificacion;
import com.example.Notificaciones.service.NotifiacionesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
public class NotifiacionesController {

    private final NotifiacionesService notifiacionesService;

    public NotifiacionesController(NotifiacionesService notifiacionesService){
        this.notifiacionesService = notifiacionesService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviar(@RequestBody Notificacion notificacion){

        notifiacionesService.enviarCorreo(
                notificacion.getDestinatario(),
                notificacion.getAsunto(),
                notificacion.getMensaje()
        );
        return ResponseEntity.ok("Notificacion procesada con exito");
    }

}
