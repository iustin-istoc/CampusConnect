package com.example.campusconnect.service;

import com.example.campusconnect.model.BackupCode;
import com.example.campusconnect.repository.BackupCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gestiunea codurilor de rezerva (backup codes).
 *  - genereaza un set de 10 coduri la configurare
 *  - verifica si consuma un cod (unica folosinta)
 */
@Service
public class BackupCodeService {

    private static final int NR_CODURI = 10;
    private static final String ALFABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // fara caractere ambigue
    private static final int LUNGIME = 8;

    private final SecureRandom random = new SecureRandom();
    private final BackupCodeRepository repo;

    public BackupCodeService(BackupCodeRepository repo) {
        this.repo = repo;
    }

    /** Genereaza un set nou de coduri (sterge codurile vechi ale utilizatorului). */
    @Transactional
    public List<String> genereazaCoduri(String email) {
        repo.deleteByEmail(email);
        List<String> rezultat = new ArrayList<>();
        for (int i = 0; i < NR_CODURI; i++) {
            String cod = genereazaUnCod();
            rezultat.add(cod);
            repo.save(new BackupCode(email, cod));
        }
        return rezultat;
    }

    /** Verifica un cod si il marcheaza ca folosit daca e valid. */
    @Transactional
    public boolean verificaSiConsuma(String email, String cod) {
        if (cod == null) return false;
        Optional<BackupCode> opt = repo.findByEmailAndCodAndFolositFalse(email, cod.trim().toUpperCase());
        if (opt.isEmpty()) return false;
        BackupCode bc = opt.get();
        bc.setFolosit(true);
        repo.save(bc);
        return true;
    }

    public long cateRamase(String email) {
        return repo.countByEmailAndFolositFalse(email);
    }

    private String genereazaUnCod() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LUNGIME; i++) {
            sb.append(ALFABET.charAt(random.nextInt(ALFABET.length())));
        }
        return sb.toString();
    }
}
