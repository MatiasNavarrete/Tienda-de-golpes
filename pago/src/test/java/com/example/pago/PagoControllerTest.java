package com.example.pago;

import com.example.pago.controller.PagoController;
import com.example.pago.dto.PagoDTO;
import com.example.pago.model.Pago;
import com.example.pago.service.PagoService;
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
class PagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagoService service;

    @InjectMocks
    private PagoController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deberiaProcesarPago() throws Exception {
        Pago pagoSimulado = new Pago();
        pagoSimulado.setId(1L);
        pagoSimulado.setPedidoId(100L);
        pagoSimulado.setMonto(25000.0);
        pagoSimulado.setMetodoPago("Tarjeta");
        pagoSimulado.setEstado("Listo");
        when(service.procesarPago(any(PagoDTO.class))).thenReturn(pagoSimulado);
        String json = """
        {
            "pedidoId": 100,
            "monto": 25000.0,
            "metodoPago": "Tarjeta"
        }
        """;
        mockMvc.perform(post("/api/pagos/procesar")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk()) // Tu API devuelve 200 OK
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Listo"))
                .andExpect(jsonPath("$.monto").value(25000.0));

        verify(service).procesarPago(any(PagoDTO.class));
    }

    @Test
    void deberiaListarHistorialPagos() throws Exception {

        Pago pago = new Pago();
        pago.setId(1L);
        pago.setPedidoId(100L);
        pago.setMonto(25000.0);
        pago.setEstado("Listo");
        when(service.listarPagos()).thenReturn(List.of(pago));
        mockMvc.perform(get("/api/pagos/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].pedidoId").value(100));

        verify(service).listarPagos();
    }
}