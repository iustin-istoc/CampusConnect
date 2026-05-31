package com.example.campusconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Inregistrarea unei tentative de acces neautorizat (alertare de securitate).
 * Salveaza fiecare login esuat sau cod MFA gresit, pentru audit.
 */
@Entity
public class AccessAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String rol;
    private String motiv;          // ex: "Parola gresita", "Cod MFA invalid"
    private String ip;
    private LocalDateTime moment;

    public AccessAlert() {}

    public AccessAlert(String email, String rol, String motiv, String ip) {
        this.email = email;
        this.rol = rol;
        this.motiv = motiv;
        this.ip = ip;
        this.moment = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getMotiv() { return motiv; }
    public void setMotiv(String motiv) { this.motiv = motiv; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public LocalDateTime getMoment() { return moment; }
    public void setMoment(LocalDateTime moment) { this.moment = moment; }
}
