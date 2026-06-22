package com.example.pedido;

import com.example.pedido.controller.PedidoController;
import com.example.pedido.dto.PedidoDTO;
import com.example.pedido.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {
    private MockMvc mockMvc;
    @Mock
    private PedidoService service;
    @InjectMocks
    private PedidoController controller;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    @Test
    void deberiaRetornarPedidoPorId() throws Exception {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setUsuarioId("user123");
        pedidoDTO.setPrecioTotal(25000.0);
        when(service.obtenerPorId(1L)).thenReturn(pedidoDTO);
        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value("user123"))
                .andExpect(jsonPath("$.precioTotal").value(25000.0));
        verify(service).obtenerPorId(1L);
    }
}