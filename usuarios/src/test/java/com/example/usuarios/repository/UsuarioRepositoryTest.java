package com.example.usuarios.repository;
import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void testGuardarYBuscarUsuario() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNombre("Matias Repo");
        usuario.setEmail("matias.repo@duoc.cl"); // Cambiado para evitar conflictos de unique en la DB
        usuario.setPassword("123456");

        // Act
        Usuario guardado = repository.save(usuario);
        Optional<Usuario> encontrado = repository.findById(guardado.getId());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Matias Repo", encontrado.get().getNombre());
    }
}