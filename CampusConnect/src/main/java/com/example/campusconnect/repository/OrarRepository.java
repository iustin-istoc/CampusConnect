package com.example.campusconnect.repository;

import com.example.campusconnect.model.Orar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrarRepository extends JpaRepository<Orar, Long> {
   // List<Orar> findByStudentEmail(String email);
    boolean existsByZiAndOraInceputAndOraSfarsitAndAnAndGrupa(
            String zi, String oraInceput, String oraSfarsit, int an, int grupa);
    List<Orar> findByProfesorId(Long profesorId);
    List<Orar> findByAnAndGrupa(int an, int grupa);


}
