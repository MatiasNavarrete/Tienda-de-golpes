package com.example.notificaciones.Controller;

import com.example.notificaciones.dto.NotificacionDto;
import com.example.notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    @PostMapping
    public void recibirNotificacion(@RequestBody NotificacionDto dto) {
        service.procesarNotificacion(dto);
    }
}