package com.example.inventario.service;

import com.example.inventario.dto.InventarioDTO;
import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void deberiaListarTodosLosInventarios() {
        // Arrange
        Inventario inv1 = new Inventario(1L, 101L, 50);
        Inventario inv2 = new Inventario(2L, 102L, 100);
        when(inventarioRepository.findAll()).thenReturn(List.of(inv1, inv2));

        // Act
        List<InventarioDTO> resultado = inventarioService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(101L, resultado.get(0).getProductoId());
        assertEquals(50, resultado.get(0).getStock());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void deberiaObtenerStockPorProductoId() {
        // Arrange
        Long productoId = 7L;
        Inventario inv = new Inventario(1L, productoId, 45);
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inv));

        // Act
        Optional<InventarioDTO> resultado = inventarioService.obtenerPorProductoId(productoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(productoId, resultado.get().getProductoId());
        assertEquals(45, resultado.get().getStock());
        verify(inventarioRepository, times(1)).findByProductoId(productoId);
    }

    @Test
    void deberiaRetornarVacioSiProductoNoExisteEnInventario() {
        // Arrange
        Long productoId = 99L;
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.empty());

        // Act
        Optional<InventarioDTO> resultado = inventarioService.obtenerPorProductoId(productoId);

        // Assert
        assertFalse(resultado.isPresent());
        verify(inventarioRepository, times(1)).findByProductoId(productoId);
    }

    @Test
    void deberiaGuardarNuevoRegistroDeInventario() {
        // Arrange
        InventarioDTO dtoIn = new InventarioDTO();
        dtoIn.setProductoId(7L);
        dtoIn.setStock(500);

        Inventario invGuardado = new Inventario(1L, 7L, 500);
        // Al ser nuevo, findByProductoId retorna Optional.empty()
        when(inventarioRepository.findByProductoId(7L)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(invGuardado);

        // Act
        InventarioDTO resultado = inventarioService.guardar(dtoIn);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(7L, resultado.getProductoId());
        assertEquals(500, resultado.getStock());
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void deberiaActualizarRegistroExistenteDeInventario() {
        // Arrange
        InventarioDTO dtoIn = new InventarioDTO();
        dtoIn.setProductoId(7L);
        dtoIn.setStock(600); // Nuevo stock

        Inventario invExistente = new Inventario(1L, 7L, 100); // stock anterior era 100
        Inventario invActualizado = new Inventario(1L, 7L, 600);

        when(inventarioRepository.findByProductoId(7L)).thenReturn(Optional.of(invExistente));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(invActualizado);

        // Act
        InventarioDTO resultado = inventarioService.guardar(dtoIn);

        // Assert
        assertNotNull(resultado);
        assertEquals(600, resultado.getStock());
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void deberiaEliminarRegistroDeInventarioYControlarExcepcion() {
        // Arrange
        Long id = 1L;
        doNothing().when(inventarioRepository).deleteById(id);

        // Act & Assert (no debería propagar excepción si ocurre un error interno en el try/catch)
        assertDoesNotThrow(() -> inventarioService.eliminar(id));
        verify(inventarioRepository, times(1)).deleteById(id);
    }
}