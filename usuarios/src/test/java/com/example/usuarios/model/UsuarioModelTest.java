package com.example.usuarios.model;

import com.example.usuarios.model.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
class UsuarioModelTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testUsuarioValido() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Fade Valido");
        usuario.setEmail("fade@valido.cl");
        usuario.setPassword("passwordPro");

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);


        assertTrue(violations.isEmpty(), "El modelo debería ser válido y no tener restricciones rotas");
    }
}