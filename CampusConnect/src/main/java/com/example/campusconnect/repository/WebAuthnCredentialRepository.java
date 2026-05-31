package com.example.campusconnect.repository;

import com.example.campusconnect.model.WebAuthnCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredential, Long> {
    List<WebAuthnCredential> findByEmail(String email);
    Optional<WebAuthnCredential> findByCredentialId(String credentialId);
    boolean existsByEmail(String email);
}
