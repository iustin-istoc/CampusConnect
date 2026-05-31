package com.example.campusconnect.controller;

import com.example.campusconnect.security.JwtUtil;
import com.example.campusconnect.service.LoginAlertService;
import com.example.campusconnect.service.WebAuthnService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * FIDO2 / WebAuthn - autentificare fara parola, cu cheie de securitate sau biometrie.
 *
 * Inregistrare (utilizator deja logat complet):
 *   POST /api/webauthn/register/start   -> optiuni + challenge
 *   POST /api/webauthn/register/finish  -> valideaza si salveaza credentialul
 *
 * Autentificare (al doilea factor, pe baza tokenului pending):
 *   POST /api/webauthn/login/start      -> challenge + credentiale permise
 *   POST /api/webauthn/login/finish     -> valideaza semnatura si emite tokenul complet
 */
@RestController
@RequestMapping("/api/webauthn")
@CrossOrigin(origins = {"http://localhost:4200", "https://campusconnect.local:4200"})
public class WebAuthnController {

    private final WebAuthnService webAuthnService;
    private final JwtUtil jwtUtil;
    private final LoginAlertService alertService;

    public WebAuthnController(WebAuthnService webAuthnService,
                              JwtUtil jwtUtil,
                              LoginAlertService alertService) {
        this.webAuthnService = webAuthnService;
        this.jwtUtil = jwtUtil;
        this.alertService = alertService;
    }

    // ── INREGISTRARE ─────────────────────────────────────────────────────────

    @PostMapping("/register/start")
    public ResponseEntity<Object> registerStart(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (email == null) return ResponseEntity.status(401).body("Neautentificat");

        Map<String, Object> resp = new HashMap<>();
        resp.put("challenge", webAuthnService.newChallenge(email));
        resp.put("rpId", webAuthnService.getRpId());
        resp.put("rpName", "CampusConnect");
        resp.put("userId", webAuthnService.userHandle(email));
        resp.put("userName", email);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register/finish")
    public ResponseEntity<Object> registerFinish(@RequestBody Map<String, String> payload,
                                                 HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (email == null) return ResponseEntity.status(401).body("Neautentificat");

        try {
            webAuthnService.finishRegistration(
                    email, payload.get("attestationObject"), payload.get("clientDataJSON"));
            return ResponseEntity.ok(Map.of("status", "REGISTERED"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Inregistrare esuata: " + e.getMessage());
        }
    }

    // ── AUTENTIFICARE ────────────────────────────────────────────────────────

    @PostMapping("/login/start")
    public ResponseEntity<Object> loginStart(@RequestBody Map<String, String> payload) {
        String pendingToken = payload.get("pendingToken");
        if (pendingToken == null || !jwtUtil.isPendingToken(pendingToken)) {
            return ResponseEntity.status(401).body("Sesiune invalida sau expirata.");
        }
        String email = jwtUtil.extractEmail(pendingToken);

        if (!webAuthnService.areCredential(email)) {
            return ResponseEntity.status(404).body("Niciun dispozitiv FIDO2 inregistrat pentru acest cont.");
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("challenge", webAuthnService.newChallenge(email));
        resp.put("rpId", webAuthnService.getRpId());
        resp.put("allowCredentials", webAuthnService.credentialIds(email));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login/finish")
    public ResponseEntity<Object> loginFinish(@RequestBody Map<String, String> payload,
                                              HttpServletRequest request) {
        String pendingToken = payload.get("pendingToken");
        if (pendingToken == null || !jwtUtil.isPendingToken(pendingToken)) {
            return ResponseEntity.status(401).body("Sesiune invalida sau expirata.");
        }
        String email = jwtUtil.extractEmail(pendingToken);
        String rol = jwtUtil.extractRol(pendingToken);

        boolean ok = webAuthnService.finishAuthentication(
                email,
                payload.get("credentialId"),
                payload.get("authenticatorData"),
                payload.get("clientDataJSON"),
                payload.get("signature"));

        if (!ok) {
            alertService.inregistreazaEsec(email, rol, "Autentificare FIDO2 esuata", request);
            return ResponseEntity.status(401).body("Verificare FIDO2 esuata.");
        }

        alertService.inregistreazaSucces(email);

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", jwtUtil.generateToken(email));
        resp.put("email", email);
        resp.put("rol", rol);
        return ResponseEntity.ok(resp);
    }
}
