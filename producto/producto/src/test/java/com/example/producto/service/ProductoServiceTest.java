package com.example.producto.service;

import com.example.producto.dto.InventarioResponse;
import com.example.producto.dto.ProductoDTO;
import com.example.producto.model.Producto;
import com.example.producto.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ProductoService productoService;

    // Mocks auxiliares necesarios para simular la estructura encadenada de WebClient
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientResponse(InventarioResponse response) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(InventarioResponse.class)).thenReturn(Mono.justOrEmpty(response));
    }
    // TEST 1: Listar todos los productos de forma exitosa fusionando BD e Inventario
    @Test
    void deberiaListarTodosLosProductosConSuStock() {
        // Arrange
        Producto p1 = new Producto();
        p1.setId(1L); p1.setNombre("Teclado"); p1.setDescripcion("RGB"); p1.setPrecio(50);

        Producto p2 = new Producto();
        p2.setId(2L); p2.setNombre("Mouse"); p2.setDescripcion("Wireless"); p2.setPrecio(30);

        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));

        InventarioResponse invMock = new InventarioResponse();
        invMock.setStock(15);
        mockWebClientResponse(invMock);

        // Act
        List<ProductoDTO> resultado = productoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(15, resultado.get(0).getStock());
        assertEquals(50, resultado.get(0).getPrecio()); // Entero
        verify(productoRepository).findAll();
    }

    // TEST 2: Buscar producto por ID existente con comunicación exitosa a inventario
    @Test
    void deberiaBuscarPorIdYRetornarProductoConStock() {
        // Arrange
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Monitor");
        producto.setPrecio(200);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        InventarioResponse invMock = new InventarioResponse();
        invMock.setStock(8);
        mockWebClientResponse(invMock);

        // Act
        Optional<ProductoDTO> resultadoOpt = productoService.buscarPorId(1L);

        // Assert
        assertTrue(resultadoOpt.isPresent());
        ProductoDTO dto = resultadoOpt.get();
        assertEquals("Monitor", dto.getNombre());
        assertEquals(200, dto.getPrecio());
        assertEquals(8, dto.getStock());
        verify(productoRepository).findById(1L);
    }

    // TEST 3: Buscar producto por ID cuando este no existe en la BD
    @Test
    void deberiaRetornarOptionalVacioCuandoProductoNoExisteEnBD() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<ProductoDTO> resultadoOpt = productoService.buscarPorId(99L);

        // Assert
        assertTrue(resultadoOpt.isEmpty());
        verify(productoRepository).findById(99L);
        verifyNoInteractions(webClient); // No debe llamar al inventario si no hay producto
    }

    // TEST 4: Guardar un nuevo producto correctamente en la BD
    @Test
    void deberiaGuardarProductoExitosamente() {
        // Arrange
        ProductoDTO inputDto = new ProductoDTO(null, "Audifonos", "Bluetooth", 100, 0);

        Producto productoGuardado = new Producto();
        productoGuardado.setId(10L);
        productoGuardado.setNombre("Audifonos");
        productoGuardado.setDescripcion("Bluetooth");
        productoGuardado.setPrecio(100);

        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        InventarioResponse invMock = new InventarioResponse();
        invMock.setStock(5);
        mockWebClientResponse(invMock);

        // Act
        ProductoDTO resultado = productoService.guardar(inputDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(100, resultado.getPrecio());
        assertEquals(5, resultado.getStock());
        verify(productoRepository).save(any(Producto.class));
    }

    // TEST 5: Tolerancia a fallos - Seguir listando u obteniendo productos aunque falle el WebClient (Microservicio caído)
    @Test
    void deberiaRetornarProductoConStockEnCeroSiFallaMicroservicioInventario() {
        // Arrange
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Gabinete");
        producto.setPrecio(90);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Simulamos que el WebClient lanza una excepción (timeout, 500 error, etc.)
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(InventarioResponse.class)).thenThrow(new RuntimeException("Connection Refused"));

        // Act
        Optional<ProductoDTO> resultadoOpt = productoService.buscarPorId(1L);

        // Assert
        assertTrue(resultadoOpt.isPresent());
        ProductoDTO dto = resultadoOpt.get();
        assertEquals(0, dto.getStock()); // Verifica el "catch" de tu código, asignando stock 0 por defecto
        assertEquals(90, dto.getPrecio());
        verify(productoRepository).findById(1L);
    }

    // TEST EXTRA: Eliminar producto por ID
    @Test
    void deberiaEliminarProductoCorrectamente() {
        // Arrange
        Long idEliminar = 1L;
        doNothing().when(productoRepository).deleteById(idEliminar);

        // Act
        productoService.eliminar(idEliminar);

        // Assert
        verify(productoRepository).deleteById(idEliminar);
    }
}