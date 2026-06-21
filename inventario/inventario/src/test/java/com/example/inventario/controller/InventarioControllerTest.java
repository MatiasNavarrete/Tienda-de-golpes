package com.example.inventario.controller;

import com.example.inventario.dto.InventarioDTO;
import com.example.inventario.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    @BeforeEach
    void setUp() {
        // Mock del contexto de la petición HTTP para que HATEOAS construya links sin fallar
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void deberiaObtenerPorProductoIdCuandoExiste() {
        // Arrange
        Long productoId = 7L;
        InventarioDTO dto = new InventarioDTO();
        dto.setId(1L);
        dto.setProductoId(productoId);
        dto.setStock(150);

        when(inventarioService.obtenerPorProductoId(productoId)).thenReturn(Optional.of(dto));

        // Act
        ResponseEntity<InventarioDTO> response = inventarioController.obtenerPorProductoId(productoId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productoId, response.getBody().getProductoId());
        assertTrue(response.getBody().hasLink("self"));
        assertTrue(response.getBody().hasLink("todos"));
        verify(inventarioService, times(1)).obtenerPorProductoId(productoId);
    }

    @Test
    void deberiaRetornarNotFoundCuandoProductoIdNoExiste() {
        // Arrange
        Long productoId = 99L;
        when(inventarioService.obtenerPorProductoId(productoId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<InventarioDTO> response = inventarioController.obtenerPorProductoId(productoId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(inventarioService, times(1)).obtenerPorProductoId(productoId);
    }

    @Test
    void deberiaListarTodosLosInventariosDelController() {
        // Arrange
        InventarioDTO dto1 = new InventarioDTO();
        dto1.setProductoId(101L);
        InventarioDTO dto2 = new InventarioDTO();
        dto2.setProductoId(102L);

        when(inventarioService.listarTodos()).thenReturn(List.of(dto1, dto2));

        // Act
        List<InventarioDTO> resultado = inventarioController.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).hasLink("self"));
        verify(inventarioService, times(1)).listarTodos();
    }

    @Test
    void deberiaCrearOActualizarStockExitosamente() {
        // Arrange
        InventarioDTO dtoIn = new InventarioDTO();
        dtoIn.setProductoId(7L);
        dtoIn.setStock(200);

        InventarioDTO dtoOut = new InventarioDTO();
        dtoOut.setId(1L);
        dtoOut.setProductoId(7L);
        dtoOut.setStock(200);

        when(inventarioService.guardar(any(InventarioDTO.class))).thenReturn(dtoOut);

        // Act
        ResponseEntity<InventarioDTO> response = inventarioController.crear(dtoIn);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertTrue(response.getBody().hasLink("self"));
        assertTrue(response.getBody().hasLink("todos"));
        verify(inventarioService, times(1)).guardar(any(InventarioDTO.class));
    }
}