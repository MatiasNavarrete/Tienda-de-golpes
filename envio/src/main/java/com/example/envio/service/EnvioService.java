package com.example.envio.service;

import com.example.envio.model.Envio;
import com.example.envio.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public Envio crearEnvio(Envio envio) {
        envio.setEstadoEnvio("preprando");
        return envioRepository.save(envio);
    }

    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }
}