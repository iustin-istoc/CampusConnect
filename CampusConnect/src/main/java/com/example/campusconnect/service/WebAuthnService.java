package com.example.campusconnect.service;

import com.example.campusconnect.model.WebAuthnCredential;
import com.example.campusconnect.repository.WebAuthnCredentialRepository;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviciu FIDO2/WebAuthn bazat pe biblioteca webauthn4j.
 *
 *  - genereaza provocari (challenge) pentru inregistrare si autentificare
 *  - valideaza raspunsurile dispozitivului (attestation / assertion)
 *  - stocheaza si actualizeaza credentialele (doar cheia publica + contor)
 *
 * RP (Relying Party) = aplicatia noastra. Pentru rulare locala:
 *   rpId   = "localhost"
 *   origin = "http://localhost:4200" (frontend-ul Angular)
 */
@Service
public class WebAuthnService {

    private static final String RP_ID = "campusconnect.local";
    private static final String ORIGIN = "https://campusconnect.local:4200";

    private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();
    private final ObjectConverter objectConverter = new ObjectConverter();
    private final AttestedCredentialDataConverter attestedCredentialDataConverter =
            new AttestedCredentialDataConverter(objectConverter);
    private final SecureRandom random = new SecureRandom();

    // challenge temporar pe email (valabil pe durata unei operatii)
    private final Map<String, Challenge> challenges = new ConcurrentHashMap<>();

    private final WebAuthnCredentialRepository repo;

    public WebAuthnService(WebAuthnCredentialRepository repo) {
        this.repo = repo;
    }

    // ── provocari ───────────────────────────────────────────────────────────

    public String newChallenge(String email) {
        Challenge challenge = new DefaultChallenge();
        challenges.put(email, challenge);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(challenge.getValue());
    }

    public String getRpId() { return RP_ID; }
    public String getOrigin() { return ORIGIN; }

    /** userHandle stabil pentru un email (necesar la inregistrare). */
    public String userHandle(String email) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(email.getBytes());
    }

    // ── INREGISTRARE ─────────────────────────────────────────────────────────

    /** Valideaza raspunsul de inregistrare si salveaza credentialul. */
    public void finishRegistration(String email, String attestationObjectB64, String clientDataB64) {
        Challenge challenge = challenges.get(email);
        if (challenge == null) throw new RuntimeException("Challenge inexistent. Reia inregistrarea.");

        byte[] attestationObject = Base64.getUrlDecoder().decode(attestationObjectB64);
        byte[] clientDataJSON = Base64.getUrlDecoder().decode(clientDataB64);

        ServerProperty serverProperty = new ServerProperty(
                new Origin(ORIGIN), RP_ID, challenge, null);

        RegistrationRequest registrationRequest =
                new RegistrationRequest(attestationObject, clientDataJSON);
        RegistrationParameters registrationParameters =
                new RegistrationParameters(serverProperty, null, false, true);

        RegistrationData data = webAuthnManager.parse(registrationRequest);
        webAuthnManager.validate(data, registrationParameters);

        AttestedCredentialData acd =
                data.getAttestationObject().getAuthenticatorData().getAttestedCredentialData();
        long signCount = data.getAttestationObject().getAuthenticatorData().getSignCount();

        byte[] serialized = attestedCredentialDataConverter.convert(acd);
        String credentialId = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(acd.getCredentialId());

        repo.save(new WebAuthnCredential(email, credentialId, serialized, signCount));
        challenges.remove(email);
    }

    // ── AUTENTIFICARE ────────────────────────────────────────────────────────

    /** Valideaza raspunsul de autentificare; intoarce true daca semnatura e corecta. */
    public boolean finishAuthentication(String email, String credentialIdB64,
                                        String authenticatorDataB64, String clientDataB64,
                                        String signatureB64) {
        Challenge challenge = challenges.get(email);
        if (challenge == null) return false;

        WebAuthnCredential stored = repo.findByCredentialId(credentialIdB64).orElse(null);
        if (stored == null || !stored.getEmail().equals(email)) return false;

        byte[] credentialId = Base64.getUrlDecoder().decode(credentialIdB64);
        byte[] authenticatorData = Base64.getUrlDecoder().decode(authenticatorDataB64);
        byte[] clientDataJSON = Base64.getUrlDecoder().decode(clientDataB64);
        byte[] signature = Base64.getUrlDecoder().decode(signatureB64);

        AttestedCredentialData acd =
                attestedCredentialDataConverter.convert(stored.getAttestedData());

        Authenticator authenticator = new AuthenticatorImpl(acd, null, stored.getSignCount());

        ServerProperty serverProperty = new ServerProperty(
                new Origin(ORIGIN), RP_ID, challenge, null);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                credentialId, authenticatorData, clientDataJSON, signature);
        AuthenticationParameters authenticationParameters = new AuthenticationParameters(
                serverProperty, authenticator, null, false, true);

        try {
            AuthenticationData data = webAuthnManager.parse(authenticationRequest);
            webAuthnManager.validate(data, authenticationParameters);

            // actualizeaza contorul de semnaturi (protectie anti-clonare)
            stored.setSignCount(data.getAuthenticatorData().getSignCount());
            repo.save(stored);
            challenges.remove(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areCredential(String email) {
        return repo.existsByEmail(email);
    }

    public java.util.List<String> credentialIds(String email) {
        return repo.findByEmail(email).stream()
                .map(WebAuthnCredential::getCredentialId)
                .toList();
    }
}
