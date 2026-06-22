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
        Pedido pedidoMock = new Pedido();
        pedidoMock.setId(1L);
        pedidoMock.setUsuarioId("user123");
        pedidoMock.setPrecioTotal(25000.0);
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(pedidoMock));
        PedidoDTO resultado = service.obtenerPorId(1L);
        assertNotNull(resultado);
        assertEquals("user123", resultado.getUsuarioId());
        assertEquals(25000.0, resultado.getPrecioTotal());
        verify(repository).findById(1L);
    }
}