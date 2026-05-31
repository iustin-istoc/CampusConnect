package com.example.campusconnect.repository;

import com.example.campusconnect.model.BackupCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BackupCodeRepository extends JpaRepository<BackupCode, Long> {
    Optional<BackupCode> findByEmailAndCodAndFolositFalse(String email, String cod);
    List<BackupCode> findByEmail(String email);
    long countByEmailAndFolositFalse(String email);
    void deleteByEmail(String email);
}
