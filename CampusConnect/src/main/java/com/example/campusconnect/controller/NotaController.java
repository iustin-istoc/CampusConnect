package com.example.campusconnect.controller;

import com.example.campusconnect.model.Nota;
import com.example.campusconnect.repository.NotaRepository;
import com.example.campusconnect.repository.ProfesorRepository;
import com.example.campusconnect.repository.StudentRepository;
import com.example.campusconnect.service.NotaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/note")
public class NotaController {

    private final NotaRepository repo;
    private final StudentRepository studentRepo;
    private final NotaService notaService;

    // Injectează NotaService în constructor
    public NotaController(NotaRepository repo, StudentRepository studentRepo, NotaService notaService) {
        this.repo = repo;
        this.studentRepo = studentRepo;
        this.notaService = notaService;
    }

    @GetMapping
    public ResponseEntity<List<Nota>> getNote(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(repo.findByStudentEmail(email));
    }

    @PostMapping
    public ResponseEntity<Object> addNota(@RequestBody Nota nota) {
        if (!studentRepo.existsByEmail(nota.getStudentEmail())) {
            return ResponseEntity.badRequest().body("Studentul nu există");
        }

        Nota saved = repo.save(nota);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/profesor/{email}")
    public ResponseEntity<List<Nota>> getNoteProfesor(@PathVariable String email) {
        List<Nota> note = repo.findByProfesorEmail(email);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> stergeNota(@PathVariable Long id) {
        notaService.stergeNota(id);
        return ResponseEntity.noContent().build();
    }
}
