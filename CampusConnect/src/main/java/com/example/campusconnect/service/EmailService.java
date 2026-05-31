package com.example.campusconnect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Serviciu generic de trimitere a email-urilor (SMTP Gmail).
 * Folosit pentru OTP pe email si pentru alertarea administratorului.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void trimite(String catre, String subiect, String continut) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(catre);
            msg.setSubject(subiect);
            msg.setText(continut);
            mailSender.send(msg);
            log.info("Email trimis catre {}", catre);
        } catch (Exception e) {
            log.error("Eroare la trimiterea email-ului catre {}: {}", catre, e.getMessage());
        }
    }
}
