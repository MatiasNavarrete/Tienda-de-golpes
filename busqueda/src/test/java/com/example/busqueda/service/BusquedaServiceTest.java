package com.example.busqueda.service;

import com.example.busqueda.model.Producto;
import com.example.busqueda.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusquedaServiceTest {

    @InjectMocks
    private BusquedaService busquedaService;

    @Mock
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarProductoExitoso() {
        Producto producto = new Producto();
        producto.setNombre("Guantes Pro");
        producto.setPrecio(new BigDecimal("29990"));

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = busquedaService.guardarProducto(producto);

        assertNotNull(resultado);
        assertEquals("Guantes Pro", resultado.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }
}