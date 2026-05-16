package com.example.envio.service;

import com.example.envio.dto.EnvioDTO;
import com.example.envio.model.Envio;
import com.example.envio.repository.EnvioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public Envio crearEnvio(EnvioDTO envioDTO) {
        log.info("Iniciando la preparación de un nuevo envío para el pedido ID: {}", envioDTO.getPedidoId());

        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setPedidoId(envioDTO.getPedidoId());
        nuevoEnvio.setDireccionDestino(envioDTO.getDireccionDestino());
        nuevoEnvio.setEstadoEnvio("Preparando");

        Envio guardado = envioRepository.save(nuevoEnvio);

        log.info("Envío guardado en BD con ID: {} hacia la dirección: {}", guardado.getId(), guardado.getDireccionDestino());
        return guardado;
    }

    public List<Envio> listarTodos() {
        log.info("Consultando el historial completo de envíos...");
        return envioRepository.findAll();
    }
}