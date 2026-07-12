package com.example.notificaciones.service;

import com.example.notificaciones.dto.NotificacionDto;
import com.example.notificaciones.model.Notificacion;
import com.example.notificaciones.repository.NotificacionRepository;
import com.example.notificaciones.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class NotificacionServiceTest {

    @InjectMocks
    private NotificacionService notificacionService;

    @Mock
    private NotificacionRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcesarNotificacionExitoso() {
        NotificacionDto dto = new NotificacionDto();
        dto.setDestinatario("fade@duoc.cl");
        dto.setMensaje("Tu cuenta ha sido creada exitosamente");

        when(repository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        notificacionService.procesarNotificacion(dto);

        verify(repository, times(1)).save(any(Notificacion.class));
    }
}