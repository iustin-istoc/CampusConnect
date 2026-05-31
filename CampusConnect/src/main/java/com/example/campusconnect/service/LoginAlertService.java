package com.example.campusconnect.service;

import com.example.campusconnect.model.AccessAlert;
import com.example.campusconnect.repository.AccessAlertRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sistem de alertare a accesului neautorizat.
 *  - salveaza fiecare tentativa esuata in baza de date (tabelul access_alert)
 *  - scrie un mesaj de avertizare in log (consola)
 *  - la depasirea unui prag de tentative consecutive, trimite o ALERTA PE EMAIL
 *    administratorului (posibil atac brute-force)
 */
@Service
public class LoginAlertService {

    private static final Logger log = LoggerFactory.getLogger(LoginAlertService.class);
    private static final int PRAG_BRUTE_FORCE = 3;

    private final AccessAlertRepository alertRepo;
    private final EmailService emailService;
    private final Map<String, Integer> incercariEsuate = new ConcurrentHashMap<>();

    /** Emailul administratorului care primeste alertele (din application.properties). */
    @Value("${app.admin-email:istociustin9@gmail.com}")
    private String adminEmail;

    public LoginAlertService(AccessAlertRepository alertRepo, EmailService emailService) {
        this.alertRepo = alertRepo;
        this.emailService = emailService;
    }

    /** Inregistreaza o tentativa esuata si genereaza alerta. */
    public void inregistreazaEsec(String email, String rol, String motiv, HttpServletRequest request) {
        String ip = extrageIp(request);
        alertRepo.save(new AccessAlert(email, rol, motiv, ip));

        int nr = incercariEsuate.merge(email == null ? "necunoscut" : email, 1, Integer::sum);

        log.warn("ALERTA SECURITATE - acces neautorizat | email={} | rol={} | motiv={} | ip={} | tentative consecutive={}",
                email, rol, motiv, ip, nr);

        if (nr >= PRAG_BRUTE_FORCE) {
            log.error("ALERTA CRITICA - posibil atac brute-force pe contul {} ({} tentative din IP {})",
                    email, nr, ip);

            emailService.trimite(
                    adminEmail,
                    "[CampusConnect] Alerta de securitate - posibil brute-force",
                    "S-au inregistrat " + nr + " tentative esuate consecutive de autentificare.\n\n" +
                            "Cont vizat : " + email + "\n" +
                            "Rol        : " + rol + "\n" +
                            "Ultim motiv: " + motiv + "\n" +
                            "Adresa IP  : " + ip + "\n" +
                            "Moment     : " + LocalDateTime.now() + "\n\n" +
                            "Verifica panoul de administrare pentru detalii."
            );
        }
    }

    /** Reseteaza contorul dupa o autentificare reusita. */
    public void inregistreazaSucces(String email) {
        if (email != null) {
            incercariEsuate.remove(email);
        }
    }

    public List<AccessAlert> toateAlertele() {
        return alertRepo.findAllByOrderByMomentDesc();
    }

    private String extrageIp(HttpServletRequest request) {
        if (request == null) return "necunoscut";
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}