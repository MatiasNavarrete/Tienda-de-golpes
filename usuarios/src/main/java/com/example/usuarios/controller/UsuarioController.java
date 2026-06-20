package com.example.usuarios.controller;

import com.example.usuarios.model.Usuario;
import com.example.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para la gestión de perfiles de la tienda")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    @Operation(summary = "Registrar un nuevo usuario", description = "Guarda el usuario en la DB, le añade enlaces HATEOAS y dispara un evento asíncrono")
    public ResponseEntity<EntityModel<Usuario>> registrar(@RequestBody Usuario usuario) {
        Usuario guardado = usuarioService.guardarUsuario(usuario);

        EntityModel<Usuario> modelo = EntityModel.of(guardado);

        modelo.add(linkTo(methodOn(UsuarioController.class).listar()).withRel("ver-todos-los-usuarios"));

        return ResponseEntity.ok(modelo);
    }

    @GetMapping("/listar")
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista con todos los usuarios registrados enriquecida con hipermedios")
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listar() {
        List<Usuario> usuarios = usuarioService.listarTodos();

        List<EntityModel<Usuario>> usuariosModelos = usuarios.stream()
                .map(u -> EntityModel.of(u,
                        linkTo(methodOn(UsuarioController.class).listar()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Usuario>> coleccion = CollectionModel.of(usuariosModelos);
        coleccion.add(linkTo(methodOn(UsuarioController.class).listar()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }
}