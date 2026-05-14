package com.example.usuarios.service;

import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario guardarUsuario(Usuario usuario){
        log.info("Intentando registrar un nuevo usuario con email: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos(){
        log.info("Consultando la lista completa de usuarios");
        return usuarioRepository.findAll();
    }
}
