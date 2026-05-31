package com.example.campusconnect.controller;

import com.example.campusconnect.model.Profesor;
import com.example.campusconnect.repository.ProfesorRepository;
import com.example.campusconnect.service.LoginAlertService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint pentru vizualizarea alertelor de securitate.
 * Accesibil DOAR administratorilor (verificat prin rolul din baza de date).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "https://campusconnect.local:4200"})
public class AlertController {

    private final LoginAlertService alertService;
    private final ProfesorRepository profesorRepo;

    public AlertController(LoginAlertService alertService, ProfesorRepository profesorRepo) {
        this.alertService = alertService;
        this.profesorRepo = profesorRepo;
    }

    @GetMapping("/alerts")
    public ResponseEntity<Object> getAlerts(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).body("Neautentificat");
        }

        Profesor p = profesorRepo.findByEmail(email);
        if (p == null || !"admin".equalsIgnoreCase(p.getRol())) {
            return ResponseEntity.status(403).body("Acces interzis - doar administratorii pot vedea alertele");
        }

        return ResponseEntity.ok(alertService.toateAlertele());
    }
}
