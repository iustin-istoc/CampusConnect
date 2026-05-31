package com.example.campusconnect.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OTP pe email - metoda alternativa de verificare in doi pasi.
 * Genereaza un cod de 6 cifre, il trimite pe email si il valideaza
 * pentru o durata limitata (5 minute).
 */
@Service
public class EmailOtpService {

    private static final long VALABILITATE_MS = 5 * 60 * 1000; // 5 minute
    private final SecureRandom random = new SecureRandom();

    // email -> (cod, momentExpirare)
    private final Map<String, CodOtp> coduri = new ConcurrentHashMap<>();

    private final EmailService emailService;

    public EmailOtpService(EmailService emailService) {
        this.emailService = emailService;
    }

    /** Genereaza un cod nou si il trimite pe email. */
    public void trimiteCod(String email) {
        String cod = String.format("%06d", random.nextInt(1_000_000));
        coduri.put(email, new CodOtp(cod, System.currentTimeMillis() + VALABILITATE_MS));

        emailService.trimite(
                email,
                "Codul tau CampusConnect",
                "Salut!\n\nCodul tau de verificare este: " + cod +
                        "\n\nEste valabil 5 minute. Daca nu ai incercat sa te autentifici, ignora acest mesaj."
        );
    }

    /** Verifica codul introdus; il invalideaza dupa o verificare reusita. */
    public boolean verificaCod(String email, String cod) {
        CodOtp stocat = coduri.get(email);
        if (stocat == null) return false;
        if (System.currentTimeMillis() > stocat.expira) {
            coduri.remove(email);
            return false;
        }
        boolean ok = stocat.cod.equals(cod == null ? "" : cod.trim());
        if (ok) coduri.remove(email);
        return ok;
    }

    private record CodOtp(String cod, long expira) {}
}
