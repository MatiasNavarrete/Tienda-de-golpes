package com.example.pago.service;

import com.example.pago.dto.PagoDTO;
import com.example.pago.model.Pago;
import com.example.pago.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public Pago procesarPago(PagoDTO pagoDTO) {
        log.info("Iniciando procesamiento de pago para el pedido ID: {} por un monto de: {}", pagoDTO.getPedidoId(), pagoDTO.getMonto());

        Pago nuevoPago = new Pago();
        nuevoPago.setPedidoId(pagoDTO.getPedidoId());
        nuevoPago.setMonto(pagoDTO.getMonto());
        nuevoPago.setMetodoPago(pagoDTO.getMetodoPago());
        nuevoPago.setEstado("Listo");

        Pago guardado = pagoRepository.save(nuevoPago);

        log.info("Pago procesado y guardado en BD con ID: {} - Estado: {}", guardado.getId(), guardado.getEstado());
        return guardado;
    }

    public List<Pago> listarPagos() {
        log.info("Consultando el historial de todos los pagos registrados...");
        return pagoRepository.findAll();
    }
}