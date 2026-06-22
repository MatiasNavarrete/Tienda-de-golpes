package com.example.envio;

import com.example.envio.controller.EnvioController;
import com.example.envio.dto.EnvioDTO;
import com.example.envio.model.Envio;
import com.example.envio.service.EnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnvioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnvioService service;

    @InjectMocks
    private EnvioController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deberiaCrearEnvio() throws Exception {
        Envio envioSimulado = new Envio();
        envioSimulado.setId(1L);
        envioSimulado.setPedidoId(100L);
        envioSimulado.setDireccionDestino("Av. San Antonio 123, Valparaíso");
        envioSimulado.setEstadoEnvio("Preparando");

        when(service.crearEnvio(any(EnvioDTO.class))).thenReturn(envioSimulado);
        String json = """
        {
            "pedidoId": 100,
            "direccionDestino": "Av. San Antonio 123, Valparaíso"
        }
        """;
        mockMvc.perform(post("/api/envios/crear")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.direccionDestino").value("Av. San Antonio 123, Valparaíso"))
                .andExpect(jsonPath("$.estadoEnvio").value("Preparando"));

        verify(service).crearEnvio(any(EnvioDTO.class));
    }

    @Test
    void deberiaListarEnvios() throws Exception {
        Envio envio = new Envio();
        envio.setId(1L);
        envio.setPedidoId(100L);
        envio.setDireccionDestino("Av. San Antonio 123, Valparaíso");
        envio.setEstadoEnvio("Preparando");
        when(service.listarTodos()).thenReturn(List.of(envio));
        mockMvc.perform(get("/api/envios/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].pedidoId").value(100))
                .andExpect(jsonPath("$[0].estadoEnvio").value("Preparando"));

        verify(service).listarTodos();
    }
}