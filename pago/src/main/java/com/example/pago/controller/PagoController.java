package com.example.pago.controller;

import com.example.pago.dto.PagoDTO;
import com.example.pago.model.Pago;
import com.example.pago.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/procesar")
    public Pago pagar(@RequestBody PagoDTO pagoDTO) {
        return pagoService.procesarPago(pagoDTO);
    }

    @GetMapping("/historial")
    public List<Pago> listar() {
        return pagoService.listarPagos();
    }
}