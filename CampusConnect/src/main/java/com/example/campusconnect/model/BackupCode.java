package com.example.campusconnect.model;

import jakarta.persistence.*;

/**
 * Cod de rezerva (backup code) de unica folosinta.
 * Se genereaza un set la configurarea MFA; fiecare cod poate fi folosit o singura data.
 */
@Entity
public class BackupCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;     // proprietarul codului
    private String cod;       // codul propriu-zis (8 caractere)
    private boolean folosit;  // true dupa ce a fost consumat

    public BackupCode() {}

    public BackupCode(String email, String cod) {
        this.email = email;
        this.cod = cod;
        this.folosit = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCod() { return cod; }
    public void setCod(String cod) { this.cod = cod; }

    public boolean isFolosit() { return folosit; }
    public void setFolosit(boolean folosit) { this.folosit = folosit; }
}
