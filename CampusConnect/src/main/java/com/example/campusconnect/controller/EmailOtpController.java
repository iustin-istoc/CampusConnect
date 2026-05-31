package com.example.campusconnect.controller;

import com.example.campusconnect.security.JwtUtil;
import com.example.campusconnect.service.EmailOtpService;
import com.example.campusconnect.service.LoginAlertService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Metoda alternativa de verificare in doi pasi: OTP pe email.
 * Functioneaza pe baza tokenului intermediar (pending) emis dupa verificarea parolei.
 *
 *   POST /api/login/email-otp/send    -> trimite codul pe email
 *   POST /api/login/email-otp/verify  -> verifica codul si emite tokenul complet
 */
@RestController
@RequestMapping("/api/login/email-otp")
@CrossOrigin(origins = {"http://localhost:4200", "https://campusconnect.local:4200"})
public class EmailOtpController {

    private final EmailOtpService emailOtpService;
    private final JwtUtil jwtUtil;
    private final LoginAlertService alertService;

    public EmailOtpController(EmailOtpService emailOtpService,
                              JwtUtil jwtUtil,
                              LoginAlertService alertService) {
        this.emailOtpService = emailOtpService;
        this.jwtUtil = jwtUtil;
        this.alertService = alertService;
    }

    @PostMapping("/send")
    public ResponseEntity<Object> send(@RequestBody Map<String, String> payload) {
        String pendingToken = payload.get("pendingToken");
        if (pendingToken == null || !jwtUtil.isPendingToken(pendingToken)) {
            return ResponseEntity.status(401).body("Sesiune invalida sau expirata.");
        }
        String email = jwtUtil.extractEmail(pendingToken);
        emailOtpService.trimiteCod(email);
        return ResponseEntity.ok(Map.of("status", "OTP_SENT", "email", email));
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verify(@RequestBody Map<String, String> payload,
                                         HttpServletRequest request) {
        String pendingToken = payload.get("pendingToken");
        String code = payload.get("code");

        if (pendingToken == null || !jwtUtil.isPendingToken(pendingToken)) {
            return ResponseEntity.status(401).body("Sesiune invalida sau expirata.");
        }

        String email = jwtUtil.extractEmail(pendingToken);
        String rol = jwtUtil.extractRol(pendingToken);

        if (!emailOtpService.verificaCod(email, code)) {
            // inregistreaza tentativa esuata (apare in panoul de admin)
            alertService.inregistreazaEsec(email, rol, "Cod email OTP invalid", request);
            return ResponseEntity.status(401).body("Cod email invalid sau expirat.");
        }

        alertService.inregistreazaSucces(email);

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", jwtUtil.generateToken(email));
        resp.put("email", email);
        resp.put("rol", rol);
        return ResponseEntity.ok(resp);
    }
}