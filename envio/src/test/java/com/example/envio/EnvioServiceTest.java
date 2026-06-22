package com.example.envio;

import com.example.envio.dto.EnvioDTO;
import com.example.envio.model.Envio;
import com.example.envio.repository.EnvioRepository;
import com.example.envio.service.EnvioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository repository;

    @InjectMocks
    private EnvioService service;

    @Test
    void deberiaCrearEnvioYAsignarEstadoPreparando() {
        EnvioDTO dto = new EnvioDTO();
        dto.setPedidoId(100L);
        dto.setDireccionDestino("Av. San Antonio 123, Valparaíso");
        Envio envioGuardado = new Envio();
        envioGuardado.setId(1L);
        envioGuardado.setPedidoId(100L);
        envioGuardado.setDireccionDestino("Av. San Antonio 123, Valparaíso");
        envioGuardado.setEstadoEnvio("Preparando");
        when(repository.save(any(Envio.class))).thenReturn(envioGuardado);
        Envio resultado = service.crearEnvio(dto);
        assertNotNull(resultado);
        assertEquals("Preparando", resultado.getEstadoEnvio());
        assertEquals("Av. San Antonio 123, Valparaíso", resultado.getDireccionDestino());
        assertEquals(100L, resultado.getPedidoId());

        verify(repository).save(any(Envio.class));
    }

    @Test
    void deberiaRetornarListaDeEnvios() {

        Envio envio = new Envio();
        envio.setId(1L);
        envio.setEstadoEnvio("Preparando");
        when(repository.findAll()).thenReturn(List.of(envio));
        List<Envio> resultado = service.listarTodos();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Preparando", resultado.get(0).getEstadoEnvio());

        verify(repository).findAll();
    }
}