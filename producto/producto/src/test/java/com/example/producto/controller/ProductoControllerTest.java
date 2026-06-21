package com.example.producto.controller;

import com.example.producto.dto.ProductoDTO;
import com.example.producto.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @Test
    void deberiaListarTodosLosProductos() {
        // Arrange
        ProductoDTO p1 = new ProductoDTO(1L, "Teclado Mecanico", "RGB Switch Red", 75, 10);
        ProductoDTO p2 = new ProductoDTO(2L, "Mouse Gamer", "16000 DPI", 45, 5);
        when(productoService.listarTodos()).thenReturn(List.of(p1, p2));

        // Act
        List<ProductoDTO> resultado = productoController.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Teclado Mecanico", resultado.get(0).getNombre());
        verify(productoService).listarTodos();
    }

    @Test
    void deberiaRetornarProductoPorIdCuandoExiste() {
        // Arrange
        ProductoDTO producto = new ProductoDTO(1L, "Monitor 4K", "32 pulgadas IPS", 350, 8);
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(producto));

        // Act
        ResponseEntity<ProductoDTO> respuesta = productoController.obtenerPorId(1L);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Monitor 4K", respuesta.getBody().getNombre());
        assertEquals(350, respuesta.getBody().getPrecio());
        verify(productoService).buscarPorId(1L);
    }

    @Test
    void deberiaRetornar404CuandoProductoNoExiste() {
        // Arrange
        when(productoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ProductoDTO> respuesta = productoController.obtenerPorId(99L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(productoService).buscarPorId(99L);
    }

    @Test
    void deberiaCrearProductoExitosamente() {
        // Arrange
        ProductoDTO inputDto = new ProductoDTO(null, "Audifonos Pro", "Cancelacion de ruido", 120, 0);
        ProductoDTO outputDto = new ProductoDTO(15L, "Audifonos Pro", "Cancelacion de ruido", 120, 0);
        when(productoService.guardar(any(ProductoDTO.class))).thenReturn(outputDto);

        // Act
        ResponseEntity<ProductoDTO> respuesta = productoController.crear(inputDto);

        // Assert
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(15L, respuesta.getBody().getId());
        assertEquals(120, respuesta.getBody().getPrecio());
        verify(productoService).guardar(any(ProductoDTO.class));
    }

    @Test
    void deberiaEliminarProductoCuandoExiste() {
        // Arrange
        ProductoDTO producto = new ProductoDTO(1L, "Eliminar", "Test", 10, 0);
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(producto));

        // Act
        ResponseEntity<Void> respuesta = productoController.eliminar(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(productoService).eliminar(1L);
    }
}