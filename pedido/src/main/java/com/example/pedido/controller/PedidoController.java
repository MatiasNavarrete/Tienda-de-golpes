package com.example.pedido.controller;

import com.example.pedido.dto.PedidoDTO;
import com.example.pedido.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/crear")
    public PedidoDTO crear(@RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.guardarPedido(pedidoDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) {
        PedidoDTO pedido = pedidoService.obtenerPorId(id);
        return pedido != null ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }


    @GetMapping("/listar")
    public List<PedidoDTO> listar() {
        return pedidoService.obtenerTodos();
    }

}