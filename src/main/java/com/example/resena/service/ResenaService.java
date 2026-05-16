package com.example.resena.service;

import com.example.resena.dto.ResenaDTO;
import com.example.resena.model.Resena;
import com.example.resena.repository.ResenaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Resena crearResena(ResenaDTO dto) {
        log.info("Verificando si el pedido ID: {} existe...", dto.getPedidoId());

        try {
            String respuestaPedido = webClientBuilder.build().get()
                    .uri("http://localhost:8086/api/pedidos/listar")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Comunicación exitosa. Guardando reseña...");
            Resena nuevaResena = new Resena();
            nuevaResena.setPedidoId(dto.getPedidoId());
            nuevaResena.setComentario(dto.getComentario());
            nuevaResena.setEstrellas(dto.getEstrellas());

            Resena guardada = resenaRepository.save(nuevaResena);
            log.info("Reseña guardada exitosamente en BD con ID: {}", guardada.getId());
            return guardada;

        } catch (Exception e) {
            log.error("Fallo de comunicación: El Pedido no está respondiendo o el ID no existe.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo verificar el pedido.");
        }
    }
    public List<Resena> listarTodas() {
        return resenaRepository.findAll();
    }
}