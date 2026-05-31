package com.example.campusconnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nume;
    private String email;
    private String parola;
    private String facultate;
    private Integer an;
    private Integer grupa;

    // ── MFA (TOTP) ──────────────────────────────────────────────
    private String mfaSecret;          // cheia secreta Base32
    private boolean mfaEnabled = false; // true dupa prima verificare reusita
}
