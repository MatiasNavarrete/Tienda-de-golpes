package com.example.carrito.service;

import com.example.carrito.dto.*;
import com.example.carrito.exception.StockInsuficienteException;
import com.example.carrito.model.Carrito;
import com.example.carrito.model.ItemCarrito;
import com.example.carrito.repository.CarritoRepository;
import com.example.carrito.repository.ItemCarritoRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private ItemCarritoRepository itemRepository;

    @InjectMocks private CarritoService carritoService;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Filtro mágico: Intercepta cualquier URL absoluta (8080, 9090) y la manda al MockWebServer de test
        ExchangeFilterFunction redirigirFiltro = (request, next) -> {
            URI uriOriginal = request.url();
            URI nuevaUri = URI.create(mockWebServer.url(uriOriginal.getRawPath()).toString());
            ClientRequest nuevaPeticion = ClientRequest.from(request).url(nuevaUri).build();
            return next.exchange(nuevaPeticion);
        };

        WebClient webClient = WebClient.builder()
                .filter(redirigirFiltro)
                .build();

        ReflectionTestUtils.setField(carritoService, "webClient", webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    // TEST 1: Obtener detalles del carrito con mapeo correcto de Producto
    @Test
    void deberiaObtenerCarritoConDetallesDeProductos() {
        // Arrange
        String usuarioId = "user123";
        Carrito carrito = new Carrito(1L, usuarioId, new ArrayList<>());
        ItemCarrito item = new ItemCarrito(10L, carrito, 7L, 2);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carrito));
        when(itemRepository.findByCarritoId(1L)).thenReturn(List.of(item));

        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":7,\"nombre\":\"Mouse Inalambrico\",\"precio\":25}")
                .addHeader("Content-Type", "application/json"));

        // Act
        CarritoDTO resultado = carritoService.obtenerCarrito(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(1, resultado.getItems().size());
        assertEquals("Mouse Inalambrico", resultado.getItems().get(0).getNombre());
        assertEquals(50, resultado.getTotalGeneral());
    }

    // TEST 2: Reducir cantidad de un producto (Quedan unidades)
    @Test
    void deberiaReducirCantidadDeUnProductoSinEliminarlo() {
        // Arrange
        String usuarioId = "user123";
        Carrito carrito = new Carrito(1L, usuarioId, new ArrayList<>());
        ItemCarrito item = new ItemCarrito(10L, carrito, 7L, 5);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carrito));
        when(itemRepository.findByCarritoIdAndProductoId(1L, 7L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(ItemCarrito.class))).thenReturn(item);
        when(itemRepository.findByCarritoId(1L)).thenReturn(List.of(item));

        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":7,\"nombre\":\"Mouse\",\"precio\":25}")
                .addHeader("Content-Type", "application/json"));

        // Act
        carritoService.reducirProducto(usuarioId, 7L, 2);

        // Assert
        assertEquals(3, item.getCantidad());
        verify(itemRepository).save(item);
    }

    // TEST 3: Reducir cantidad hasta 0 o menos (Debe eliminar el ítem)
    @Test
    void deberiaEliminarItemDelCarritoSiCantidadLlegaACeroOMenos() {
        // Arrange
        String usuarioId = "user123";
        Carrito carrito = new Carrito(1L, usuarioId, new ArrayList<>());
        ItemCarrito item = new ItemCarrito(10L, carrito, 7L, 2);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carrito));
        when(itemRepository.findByCarritoIdAndProductoId(1L, 7L)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).delete(item);
        when(itemRepository.findByCarritoId(1L)).thenReturn(List.of());

        // Act
        carritoService.reducirProducto(usuarioId, 7L, 2);

        // Assert
        verify(itemRepository).delete(item);
    }

    // TEST 4: Agregar producto nuevo arrojando StockInsuficienteException si supera inventario
    @Test
    void deberiaLanzarExcepcionAlAgregarSiNoHayStockSuficiente() {
        // Arrange
        String usuarioId = "user123";
        Carrito carrito = new Carrito(1L, usuarioId, new ArrayList<>());
        ItemRequestDTO request = new ItemRequestDTO();
        request.setProductoId(7L); request.setCantidad(10);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carrito));
        when(itemRepository.findByCarritoIdAndProductoId(1L, 7L)).thenReturn(Optional.empty());

        // 1. Respuesta de MS-PRODUCTOS
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":7,\"nombre\":\"Mouse\",\"precio\":25}")
                .addHeader("Content-Type", "application/json"));
        // 2. Respuesta de MS-INVENTARIO (Solo hay 5 en stock)
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"stock\":5}")
                .addHeader("Content-Type", "application/json"));

        // Act & Assert
        assertThrows(StockInsuficienteException.class, () -> {
            carritoService.agregarProducto(usuarioId, request);
        });
        verify(itemRepository, never()).save(any(ItemCarrito.class));
    }

    // TEST 5: Vaciar carrito exitosamente
    @Test
    void deberiaVaciarTodosLosItemsDelCarrito() {
        // Arrange
        String usuarioId = "user123";
        Carrito carrito = new Carrito(1L, usuarioId, new ArrayList<>());
        ItemCarrito item1 = new ItemCarrito(10L, carrito, 1L, 1);
        List<ItemCarrito> items = List.of(item1);

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carrito));
        when(itemRepository.findByCarritoId(1L)).thenReturn(items);
        doNothing().when(itemRepository).deleteAll(items);

        // Act
        carritoService.vaciarCarrito(usuarioId);

        // Assert
        verify(itemRepository).deleteAll(items);
    }
}