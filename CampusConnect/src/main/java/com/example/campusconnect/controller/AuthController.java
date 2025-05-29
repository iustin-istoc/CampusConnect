package com.example.campusconnect.controller;

import com.example.campusconnect.security.JwtUtil;
import com.example.campusconnect.model.Student;
import com.example.campusconnect.model.Profesor;
import com.example.campusconnect.repository.StudentRepository;
import com.example.campusconnect.repository.ProfesorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final StudentRepository studentRepo;
    private final ProfesorRepository profesorRepo;
    private final JwtUtil jwtUtil;

    public AuthController(StudentRepository studentRepo, ProfesorRepository profesorRepo, JwtUtil jwtUtil) {
        this.studentRepo = studentRepo;
        this.profesorRepo = profesorRepo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String parola = payload.get("parola");
        String rol = payload.get("rol");

        if ("student".equalsIgnoreCase(rol)) {
            Optional<Student> student = studentRepo.findByEmailAndParola(email, parola);
            if (student.isPresent()) {
                String token = jwtUtil.generateToken(email);
                return ResponseEntity.ok(Map.of("token", token, "email", email, "rol", "student"));
            } else {
                return ResponseEntity.status(401).body("Student invalid");
            }
        } else if ("profesor".equalsIgnoreCase(rol)) {
            Optional<Profesor> profesor = profesorRepo.findByEmailAndParola(email, parola);
            if (profesor.isPresent()) {
                String token = jwtUtil.generateToken(email);
                return ResponseEntity.ok(Map.of("token", token, "email", email, "rol", "profesor"));
            } else {
                return ResponseEntity.status(401).body("Profesor invalid");
            }
        }

        return ResponseEntity.badRequest().body("Rol necunoscut");
    }
}
