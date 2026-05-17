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
        log.info("Verificando existencia del pedido ID: {} en ms-pedido...", dto.getPedidoId());

        try {

            webClientBuilder.build().get()
                    .uri("http://localhost:8086/api/pedidos/{id}", dto.getPedidoId())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Pedido verificado con éxito en el sistema. Procediendo a guardar la reseña.");

            Resena nuevaResena = new Resena();
            nuevaResena.setPedidoId(dto.getPedidoId());
            nuevaResena.setComentario(dto.getComentario());
            nuevaResena.setEstrellas(dto.getEstrellas());

            Resena guardada = resenaRepository.save(nuevaResena);
            log.info("Reseña guardada exitosamente en BD con ID: {}", guardada.getId());
            return guardada;

        } catch (Exception e) {
            log.error("Validación fallida: El Pedido ID {} no existe en la base de datos o ms-pedido está apagado.", dto.getPedidoId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede dejar una reseña: El ID de pedido no existe.");
        }
    }
    public List<Resena> listarTodas() {
        return resenaRepository.findAll();
    }
}