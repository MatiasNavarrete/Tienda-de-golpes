package com.example.busqueda.repository;

import com.example.busqueda.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void testBuscarPorNombreContaining() {
        Producto producto = new Producto();
        producto.setNombre("Guantes Pro Strike");
        producto.setCategoria("Proteccion");
        producto.setPrecio(new BigDecimal("25000"));
        producto.setStock(10);
        productoRepository.save(producto);

        List<Producto> encontrados = productoRepository.findByNombreContainingIgnoreCase("strike");

        assertNotNull(encontrados);
        assertFalse(encontrados.isEmpty());
    }
}