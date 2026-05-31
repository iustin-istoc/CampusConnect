package com.example.campusconnect.controller;

import com.example.campusconnect.security.JwtUtil;
import com.example.campusconnect.service.BackupCodeService;
import com.example.campusconnect.service.LoginAlertService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coduri de rezerva (backup codes) - metoda de autentificare de rezerva
 * cand utilizatorul nu are acces la aplicatia TOTP sau la email.
 *
 *   POST /api/login/backup/generate  -> genereaza un set nou (necesita token complet)
 *   POST /api/login/backup/verify    -> foloseste un cod ca al doilea factor (token pending)
 */
@RestController
@RequestMapping("/api/login/backup")
@CrossOrigin(origins = {"http://localhost:4200", "https://campusconnect.local:4200"})
public class BackupCodeController {

    private final BackupCodeService backupService;
    private final JwtUtil jwtUtil;
    private final LoginAlertService alertService;

    public BackupCodeController(BackupCodeService backupService,
                                JwtUtil jwtUtil,
                                LoginAlertService alertService) {
        this.backupService = backupService;
        this.jwtUtil = jwtUtil;
        this.alertService = alertService;
    }

    /** Genereaza un set nou de coduri pentru utilizatorul deja autentificat complet. */
    @PostMapping("/generate")
    public ResponseEntity<Object> generate(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).body("Neautentificat");
        }
        List<String> coduri = backupService.genereazaCoduri(email);
        return ResponseEntity.ok(Map.of("coduri", coduri));
    }

    /** Foloseste un cod de rezerva ca al doilea factor in fluxul de login. */
    @PostMapping("/verify")
    public ResponseEntity<Object> verify(@RequestBody Map<String, String> payload,
                                         HttpServletRequest request) {
        String pendingToken = payload.get("pendingToken");
        String cod = payload.get("code");

        if (pendingToken == null || !jwtUtil.isPendingToken(pendingToken)) {
            return ResponseEntity.status(401).body("Sesiune invalida sau expirata.");
        }

        String email = jwtUtil.extractEmail(pendingToken);
        String rol = jwtUtil.extractRol(pendingToken);

        if (!backupService.verificaSiConsuma(email, cod)) {
            alertService.inregistreazaEsec(email, rol, "Cod de rezerva invalid", request);
            return ResponseEntity.status(401).body("Cod de rezerva invalid sau deja folosit.");
        }

        alertService.inregistreazaSucces(email);

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", jwtUtil.generateToken(email));
        resp.put("email", email);
        resp.put("rol", rol);
        resp.put("coduriRamase", backupService.cateRamase(email));
        return ResponseEntity.ok(resp);
    }
}
