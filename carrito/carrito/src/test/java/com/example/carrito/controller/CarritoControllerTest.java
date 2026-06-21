package com.example.carrito.controller;

import com.example.carrito.dto.CarritoDTO;
import com.example.carrito.dto.ItemRequestDTO;
import com.example.carrito.service.CarritoService;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoControllerTest {

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private CarritoController carritoController;

    @BeforeEach
    void setUp() {
        // Necesario para que Spring HATEOAS construya los enlaces (linkTo) sin fallar por falta de contexto HTTP
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void deberiaObtenerCarritoDelUsuario() {
        // Arrange
        String usuarioId = "user123";
        CarritoDTO mockCarrito = new CarritoDTO(1L, usuarioId, new ArrayList<>());
        when(carritoService.obtenerCarrito(usuarioId)).thenReturn(mockCarrito);

        // Act
        ResponseEntity<CarritoDTO> response = carritoController.obtenerCarrito(usuarioId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioId, response.getBody().getUsuarioId());
        assertTrue(response.getBody().hasLink("self"));
        assertTrue(response.getBody().hasLink("vaciar"));
        verify(carritoService, times(1)).obtenerCarrito(usuarioId);
    }

    @Test
    void deberiaAgregarProductoAlCarrito() {
        // Arrange
        String usuarioId = "user123";
        ItemRequestDTO requestDTO = new ItemRequestDTO();
        requestDTO.setProductoId(7L);
        requestDTO.setCantidad(3);

        CarritoDTO mockCarrito = new CarritoDTO(1L, usuarioId, new ArrayList<>());
        when(carritoService.agregarProducto(eq(usuarioId), any(ItemRequestDTO.class))).thenReturn(mockCarrito);

        // Act
        ResponseEntity<CarritoDTO> response = carritoController.agregarProducto(usuarioId, requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().hasLink("self"));
        verify(carritoService, times(1)).agregarProducto(eq(usuarioId), any(ItemRequestDTO.class));
    }

    @Test
    void deberiaReducirProductoDelCarrito() {
        // Arrange
        String usuarioId = "user123";
        Long productoId = 7L;
        int cantidadAReducir = 2;

        CarritoDTO mockCarrito = new CarritoDTO(1L, usuarioId, new ArrayList<>());
        when(carritoService.reducirProducto(usuarioId, productoId, cantidadAReducir)).thenReturn(mockCarrito);

        // Act
        ResponseEntity<CarritoDTO> response = carritoController.reducirProducto(usuarioId, productoId, cantidadAReducir);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().hasLink("self"));
        verify(carritoService, times(1)).reducirProducto(usuarioId, productoId, cantidadAReducir);
    }

    @Test
    void deberiaEliminarProductoCompleto() {
        // Arrange
        String usuarioId = "user123";
        Long productoId = 7L;
        doNothing().when(carritoService).eliminarProducto(usuarioId, productoId);

        // Act
        ResponseEntity<Void> response = carritoController.eliminarProducto(usuarioId, productoId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(carritoService, times(1)).eliminarProducto(usuarioId, productoId);
    }

    @Test
    void deberiaVaciarCarritoCompleto() {
        // Arrange
        String usuarioId = "user123";
        doNothing().when(carritoService).vaciarCarrito(usuarioId);

        // Act
        ResponseEntity<Void> response = carritoController.vaciarCarrito(usuarioId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(carritoService, times(1)).vaciarCarrito(usuarioId);
    }
}