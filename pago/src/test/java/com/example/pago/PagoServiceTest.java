package com.example.pago;

import com.example.pago.dto.PagoDTO;
import com.example.pago.model.Pago;
import com.example.pago.repository.PagoRepository;
import com.example.pago.service.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository repository;

    @InjectMocks
    private PagoService service;

    @Test
    void deberiaProcesarPagoYAsignarEstadoListo() {

        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(100L);
        dto.setMonto(25000.0);
        dto.setMetodoPago("Tarjeta");

        Pago pagoGuardado = new Pago();
        pagoGuardado.setId(1L);
        pagoGuardado.setPedidoId(100L);
        pagoGuardado.setMonto(25000.0);
        pagoGuardado.setMetodoPago("Tarjeta");
        pagoGuardado.setEstado("Listo");
        when(repository.save(any(Pago.class))).thenReturn(pagoGuardado);
        Pago resultado = service.procesarPago(dto);
        assertNotNull(resultado);
        assertEquals("Listo", resultado.getEstado());
        assertEquals(25000.0, resultado.getMonto());
        assertEquals(100L, resultado.getPedidoId());

        verify(repository).save(any(Pago.class));
    }

    @Test
    void deberiaRetornarListaDePagos() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setEstado("Listo");

        when(repository.findAll()).thenReturn(List.of(pago));
        List<Pago> resultado = service.listarPagos();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Listo", resultado.get(0).getEstado());

        verify(repository).findAll();
    }
}