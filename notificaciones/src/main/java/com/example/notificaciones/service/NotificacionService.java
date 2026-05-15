package com.example.notificaciones.service;

import com.example.notificaciones.dto.NotificacionDto;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.repository.NotificacionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificacionService {

    @Autowired
    private NotificacionRepository repository;

    public void procesarNotificacion(NotificacionDto dto) {
        log.info("Recibida petición de notificación para: {}", dto.getDestinatario());

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(dto.getDestinatario());
        notificacion.setMensaje(dto.getMensaje());

        repository.save(notificacion);
        log.info("Notificación guardada en base de datos correctamente");
    }
}