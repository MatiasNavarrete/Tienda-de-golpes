package com.example.producto.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import com.example.producto.dto.ProductoDTO;
import com.example.producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/productos")
@Slf4j
@Tag(name = "Producto Controller", description = "Endpoints para la gestión del catálogo de productos") // Adaptado de la guía
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listar todos los productos", description = "Recupera todos los productos registrados en el catálogo general.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public List<ProductoDTO> listar() {
        log.info("Petición GET recibida lista para listar todos los productos");
        return productoService.listarTodos().stream()
                .map(producto -> {
                    Long id = producto.getId();
                    producto.add(linkTo(methodOn(ProductoController.class).obtenerPorId(id)).withSelfRel());
                    producto.add(linkTo(methodOn(ProductoController.class).actualizar(id, producto)).withRel("update"));
                    producto.add(linkTo(methodOn(ProductoController.class).eliminar(id)).withRel("delete"));
                    return producto;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca un producto específico utilizando su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición Get recibida para obtener producto con ID: {}", id);
        return productoService.buscarPorId(id)
                .map(producto -> {
                    producto.add(linkTo(methodOn(ProductoController.class).obtenerPorId(id)).withSelfRel());
                    producto.add(linkTo(methodOn(ProductoController.class).listar()).withRel("todos"));
                    producto.add(linkTo(methodOn(ProductoController.class).actualizar(id, producto)).withRel("update"));
                    producto.add(linkTo(methodOn(ProductoController.class).eliminar(id)).withRel("delete"));

                    log.debug("Producto con ID {} encontrado exitosamente", id);
                    return new ResponseEntity<>(producto, HttpStatus.OK);
                })
                .orElseGet(() -> {
                 log.warn("Petición Get fallida: Producto con ID {} no encontrado", id);
                 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @Operation(summary = "Crear un nuevo producto", description = "Registra un nuevo artículo en el catálogo de la tienda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Petición POST recibida para crear un nuevo producto: {}", productoDTO.getNombre());
        ProductoDTO guardado = productoService.guardar(productoDTO);

        guardado.add(linkTo(methodOn(ProductoController.class).listar()).withRel("todos"));
        guardado.add(linkTo(methodOn(ProductoController.class).obtenerPorId(guardado.getId())).withSelfRel());
        guardado.add(linkTo(methodOn(ProductoController.class).actualizar(guardado.getId(), guardado)).withRel("update"));
        guardado.add(linkTo(methodOn(ProductoController.class).eliminar(guardado.getId())).withRel("delete"));

        log.debug("Producto creado con éxito. ID asignado: {}", guardado.getId());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un producto", description = "Modifica los datos de un producto existente basándose en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado para actualizar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("Petición PUT recibida para actualizar producto con ID: {}", id);
        return productoService.buscarPorId(id)
                .map(p -> {
                    productoDTO.setId(id);
                    ProductoDTO actualizado = productoService.guardar(productoDTO);
                    actualizado.add(linkTo(methodOn(ProductoController.class).listar()).withRel("todos"));
                    actualizado.add(linkTo(methodOn(ProductoController.class).obtenerPorId(actualizado.getId())).withSelfRel());
                    actualizado.add(linkTo(methodOn(ProductoController.class).eliminar(actualizado.getId())).withRel("delete"));

                    log.debug("Producto con ID {} actualizado correctamente", id);
                    return new ResponseEntity<>(actualizado, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Petición PUT fallida: Imposible actualizar, producto ID {} no existe", id);
                    return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @Operation(summary = "Eliminar un producto", description = "Remueve permanentemente un producto del catálogo del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición DELETE recibida para eliminar producto con ID: {}", id);
        if (productoService.buscarPorId(id).isPresent()) {
            productoService.eliminar(id);
            log.debug("Producto con ID {} eliminado del correctamente del sistema", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.warn("Petición DELETE fallida: Producto ID {} no encontrado para eliminar", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}