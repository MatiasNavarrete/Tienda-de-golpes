package com.example.pedido;

import com.example.pedido.dto.PedidoDTO;
import com.example.pedido.model.Pedido;
import com.example.pedido.repository.PedidoRepository;
import com.example.pedido.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @InjectMocks
    private PedidoService service;

    @Test
    void deberiaRetornarPedidoCuandoExistePorId() {
        // Given (Preparación del escenario usando tus datos reales)
        Pedido pedidoMock = new Pedido();
        pedidoMock.setId(1L);
        pedidoMock.setUsuarioId("user123");
        pedidoMock.setPrecioTotal(25000.0);

        // When (Configuración del comportamiento del Mock del Repositorio)
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(pedidoMock));

        // Act (Llamada al método de tu capa Service)
        PedidoDTO resultado = service.obtenerPorId(1L);

        // Assert (Validaciones basadas en Given-When-Then requeridas por la rúbrica)
        assertNotNull(resultado);
        assertEquals("user123", resultado.getUsuarioId());
        assertEquals(25000.0, resultado.getPrecioTotal());

        // Verificación de interacción exigida por el indicador IE 3.1.2
        verify(repository).findById(1L);
    }
}