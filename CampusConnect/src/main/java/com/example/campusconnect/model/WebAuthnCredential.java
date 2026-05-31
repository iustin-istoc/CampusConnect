package com.example.campusconnect.model;

import jakarta.persistence.*;

/**
 * Credential FIDO2/WebAuthn inregistrat de un utilizator
 * (cheie de securitate fizica, Windows Hello, Touch ID, passkey de telefon etc.).
 *
 * Pe server se stocheaza DOAR cheia publica - cheia privata nu paraseste niciodata
 * dispozitivul utilizatorului. Aceasta este esenta securitatii FIDO2.
 */
@Entity
public class WebAuthnCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;          // proprietarul

    @Column(length = 512)
    private String credentialId;   // identificatorul credentialului (Base64URL)

    @Lob
    @Column(length = 4096)
    private byte[] attestedData;    // datele credentialului (cheie publica COSE), serializate

    private long signCount;        // contorul de semnaturi (anti-clonare)

    public WebAuthnCredential() {}

    public WebAuthnCredential(String email, String credentialId, byte[] attestedData, long signCount) {
        this.email = email;
        this.credentialId = credentialId;
        this.attestedData = attestedData;
        this.signCount = signCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }

    public byte[] getAttestedData() { return attestedData; }
    public void setAttestedData(byte[] attestedData) { this.attestedData = attestedData; }

    public long getSignCount() { return signCount; }
    public void setSignCount(long signCount) { this.signCount = signCount; }
}
