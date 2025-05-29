package com.example.campusconnect.controller;

import com.example.campusconnect.model.Profesor;
import com.example.campusconnect.service.ProfesorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.campusconnect.repository.ProfesorRepository;

import java.util.List;

@RestController
@RequestMapping("/api/profesori")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")

public class ProfesorController {

    private final ProfesorService service;

    private final ProfesorRepository profesorRepository;

    public ProfesorController(ProfesorService service, ProfesorRepository profesorRepository) {
        this.service = service;
        this.profesorRepository = profesorRepository;
    }


    @GetMapping
    public List<Profesor> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Profesor save(@RequestBody Profesor profesor) {
        return service.save(profesor);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getProfesorInfo(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Profesor profesor = profesorRepository.findByEmail(email);
        if (profesor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profesorul nu a fost găsit");
        }
        return ResponseEntity.ok(profesor);
    }

    @GetMapping("/{email}")
    public Profesor getByEmail(@PathVariable String email) {
        return service.getByEmail(email);
    }
}
