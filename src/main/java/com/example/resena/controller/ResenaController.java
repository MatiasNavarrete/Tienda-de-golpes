package com.example.resena.controller;

import com.example.resena.dto.ResenaDTO;
import com.example.resena.model.Resena;
import com.example.resena.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @PostMapping("/crear")
    public ResponseEntity<Resena> crear(@Valid @RequestBody ResenaDTO resenaDTO) {
        return new ResponseEntity<>(resenaService.crearResena(resenaDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Resena>> listar() {
        return ResponseEntity.ok(resenaService.listarTodas());
    }
}