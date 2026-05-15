package com.example.usuarios.service;

import com.example.usuarios.dto.NotificacionDto;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private WebClient webClient;

    public Usuario guardarUsuario(Usuario usuario) {
        Usuario guardado = usuarioRepository.save(usuario);

        NotificacionDto aviso = new NotificacionDto();
        aviso.setDestinatario(guardado.getEmail());
        aviso.setAsunto("Bienvenido");
        aviso.setMensaje("Hola " + guardado.getNombre() + ", tu cuenta ha sido creada.");

        webClient.post()
                .bodyValue(aviso)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        return guardado;
    }
    public List<Usuario> listarTodos(){
        log.info("Consultando la lista completa de usuarios");
        return usuarioRepository.findAll();
    }
}
