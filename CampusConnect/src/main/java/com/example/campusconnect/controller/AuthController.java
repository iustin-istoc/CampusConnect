package com.example.campusconnect.controller;

import com.example.campusconnect.model.Profesor;
import com.example.campusconnect.model.Student;
import com.example.campusconnect.repository.ProfesorRepository;
import com.example.campusconnect.repository.StudentRepository;
import com.example.campusconnect.security.JwtUtil;
import com.example.campusconnect.security.TotpService;
import com.example.campusconnect.service.LoginAlertService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Autentificare in doi pasi cu MFA (TOTP / Google Authenticator):
 *   Pas 1  POST /api/login         -> verifica email + parola; cere codul MFA.
 *   Pas 2  POST /api/login/verify  -> verifica codul TOTP; emite tokenul complet.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "https://campusconnect.local:4200"})
public class AuthController {

    private final StudentRepository studentRepo;
    private final ProfesorRepository profesorRepo;
    private final JwtUtil jwtUtil;
    private final TotpService totpService;
    private final LoginAlertService alertService;

    public AuthController(StudentRepository studentRepo,
                          ProfesorRepository profesorRepo,
                          JwtUtil jwtUtil,
                          TotpService totpService,
                          LoginAlertService alertService) {
        this.studentRepo = studentRepo;
        this.profesorRepo = profesorRepo;
        this.jwtUtil = jwtUtil;
        this.totpService = totpService;
        this.alertService = alertService;
    }

    // ── PAS 1: verificare parola ───────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload,
                                        HttpServletRequest request) {
        String email = payload.get("email");
        String parola = payload.get("parola");
        String rol = payload.get("rol");

        if ("student".equalsIgnoreCase(rol)) {
            Optional<Student> opt = studentRepo.findByEmailAndParola(email, parola);
            if (opt.isEmpty()) return esec(email, "student", "Parola gresita (student)", request);
            Student s = opt.get();
            return pasMfa(email, "student",
                    s.getMfaSecret(), s.isMfaEnabled(),
                    secret -> { s.setMfaSecret(secret); studentRepo.save(s); });

        } else if ("profesor".equalsIgnoreCase(rol)) {
            Optional<Profesor> opt = profesorRepo.findByEmailAndParola(email, parola);
            if (opt.isEmpty()) return esec(email, "profesor", "Parola gresita (profesor)", request);
            Profesor p = opt.get();
            return pasMfa(email, "profesor",
                    p.getMfaSecret(), p.isMfaEnabled(),
                    secret -> { p.setMfaSecret(secret); profesorRepo.save(p); });

        } else if ("admin".equalsIgnoreCase(rol)) {
            Optional<Profesor> opt = profesorRepo.findByEmailAndParolaAndRol(email, parola, "admin");
            if (opt.isEmpty()) return esec(email, "admin", "Parola gresita (admin)", request);
            Profesor p = opt.get();
            return pasMfa(email, "admin",
                    p.getMfaSecret(), p.isMfaEnabled(),
                    secret -> { p.setMfaSecret(secret); profesorRepo.save(p); });
        }

        return ResponseEntity.badRequest().body("Rol necunoscut");
    }

    // ── PAS 2: verificare cod TOTP ─────────────────────────────────────────
    @PostMapping("/login/verify")
    public ResponseEntity<Object> verify(@RequestBody Map<String, String> payload,
                                         HttpServletRequest request) {
        String pendingToken = payload.get("pendingToken");
        String code = payload.get("code");

        if (pendingToken == null || !jwtUtil.isPendingToken(pendingToken)) {
            return ResponseEntity.status(401).body("Sesiune MFA invalida sau expirata. Reia autentificarea.");
        }

        String email = jwtUtil.extractEmail(pendingToken);
        String rol = jwtUtil.extractRol(pendingToken);

        // recupereaza secretul utilizatorului
        String secret;
        Runnable activeazaMfa;
        if ("student".equalsIgnoreCase(rol)) {
            Student s = studentRepo.findByEmail(email);
            if (s == null) return ResponseEntity.status(401).body("Cont inexistent.");
            secret = s.getMfaSecret();
            activeazaMfa = () -> { s.setMfaEnabled(true); studentRepo.save(s); };
        } else {
            Profesor p = profesorRepo.findByEmail(email);
            if (p == null) return ResponseEntity.status(401).body("Cont inexistent.");
            secret = p.getMfaSecret();
            activeazaMfa = () -> { p.setMfaEnabled(true); profesorRepo.save(p); };
        }

        if (!totpService.verifyCode(secret, code)) {
            alertService.inregistreazaEsec(email, rol, "Cod MFA invalid", request);
            return ResponseEntity.status(401).body("Cod MFA invalid.");
        }

        // cod corect -> activeaza definitiv MFA (la prima configurare) si emite tokenul complet
        activeazaMfa.run();
        alertService.inregistreazaSucces(email);

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", jwtUtil.generateToken(email));
        resp.put("email", email);
        resp.put("rol", rol);
        return ResponseEntity.ok(resp);
    }

    // ── helper-e ───────────────────────────────────────────────────────────

    /** Parola e corecta: decide intre configurare MFA (prima data) si cerere cod (ulterior). */
    private ResponseEntity<Object> pasMfa(String email, String rol,
                                          String secretCurent, boolean mfaEnabled,
                                          java.util.function.Consumer<String> salveazaSecret) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("pendingToken", jwtUtil.generatePendingToken(email, rol));
        resp.put("rol", rol);

        if (!mfaEnabled) {
            // prima autentificare: genereaza secret + QR de scanat
            String secret = (secretCurent != null) ? secretCurent : totpService.generateSecret();
            salveazaSecret.accept(secret);
            resp.put("status", "MFA_SETUP");
            resp.put("qr", totpService.generateQrBase64(email, secret));
            resp.put("secret", secret); // afisat ca alternativa daca nu poate scana QR-ul
        } else {
            resp.put("status", "MFA_REQUIRED");
        }
        return ResponseEntity.ok(resp);
    }

    private ResponseEntity<Object> esec(String email, String rol, String motiv, HttpServletRequest request) {
        alertService.inregistreazaEsec(email, rol, motiv, request);
        return ResponseEntity.status(401).body("Autentificare esuata.");
    }
}
