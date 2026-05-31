package com.example.campusconnect.model;

import jakarta.persistence.*;

@Entity
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nume;
    private String email;
    private String parola;
    private String rol;

    @ManyToOne
    private Specializare specializare;

    @ManyToOne
    private Materie materie;

    // ── MFA (TOTP) ──────────────────────────────────────────────
    private String mfaSecret;
    private boolean mfaEnabled = false;

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }

    public Specializare getSpecializare() { return specializare; }
    public void setSpecializare(Specializare specializare) { this.specializare = specializare; }

    public Materie getMaterie() { return materie; }
    public void setMaterie(Materie materie) { this.materie = materie; }

    public String getMfaSecret() { return mfaSecret; }
    public void setMfaSecret(String mfaSecret) { this.mfaSecret = mfaSecret; }

    public boolean isMfaEnabled() { return mfaEnabled; }
    public void setMfaEnabled(boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; }
}
