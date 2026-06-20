package com.example.busqueda.controller;


import com.example.busqueda.model.Producto;
import com.example.busqueda.service.BusquedaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BusquedaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BusquedaService busquedaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testEndpointIndexar() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Saco de Boxeo");
        producto.setPrecio(new BigDecimal("45000"));

        when(busquedaService.guardarProducto(any(Producto.class))).thenReturn(producto);

        // Act & Assert
        mockMvc.perform(post("/api/busqueda/admin/indexar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Saco de Boxeo"))
                .andExpect(jsonPath("$._links.catalogo-completo.href").exists()); // Valida HATEOAS en Búsqueda
    }
}