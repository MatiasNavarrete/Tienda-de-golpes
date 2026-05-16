package com.example.envio.controller;

import com.example.envio.dto.EnvioDTO;
import com.example.envio.model.Envio;
import com.example.envio.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @PostMapping("/crear")
    public Envio crear(@RequestBody EnvioDTO envioDTO) {
        return envioService.crearEnvio(envioDTO);
    }

    @GetMapping("/listar")
    public List<Envio> listar() {
        return envioService.listarTodos();
    }
}