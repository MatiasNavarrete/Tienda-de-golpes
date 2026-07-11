package com.example.pedido;

import com.example.pedido.dto.CarritoResponseDTO;
import com.example.pedido.dto.PedidoDTO;
import com.example.pedido.model.Pedido;
import com.example.pedido.repository.PedidoRepository;
import com.example.pedido.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository repository;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PedidoService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void deberiaRetornarPedidoCuandoExistePorId() {
        Pedido pedidoMock = new Pedido(1L, "user123", 25000.0);
        when(repository.findById(1L)).thenReturn(Optional.of(pedidoMock));

        PedidoDTO resultado = service.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("user123", resultado.getUsuarioId());
        assertEquals(25000.0, resultado.getPrecioTotal());
    }

    @Test
    void deberiaObtenerTodosLosPedidos() {
        when(repository.findAll()).thenReturn(List.of(new Pedido(1L, "user1", 100.0)));
        List<PedidoDTO> res = service.obtenerTodos();
        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
    }

    @Test
    void guardarPedido_deberiaLanzarExcepcionCuandoCarritoEstaVacio() {
        CarritoResponseDTO carritoVacio = new CarritoResponseDTO();
        carritoVacio.setItems(Collections.emptyList());

        when(responseSpec.bodyToMono(CarritoResponseDTO.class)).thenReturn(Mono.just(carritoVacio));

        PedidoDTO input = new PedidoDTO(null, "user123", 0.0);

        Exception exception = assertThrows(RuntimeException.class, () -> service.guardarPedido(input));
        assertTrue(exception.getMessage().contains("vacío"));
    }

    @Test
    void guardarPedido_deberiaLanzarExcepcionCuandoMsCarritoFalla() {
        when(responseSpec.bodyToMono(CarritoResponseDTO.class)).thenThrow(new RuntimeException("Connection error"));

        PedidoDTO input = new PedidoDTO(null, "user123", 0.0);

        Exception exception = assertThrows(RuntimeException.class, () -> service.guardarPedido(input));
        assertEquals("El microservicio de carrito no responde.", exception.getMessage());
    }
}