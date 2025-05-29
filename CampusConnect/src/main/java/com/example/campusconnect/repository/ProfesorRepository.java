package com.example.campusconnect.repository;

import com.example.campusconnect.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Optional<Profesor> findByEmailAndParola(String email, String parola);
    Profesor findByEmail(String email);
    Optional<Profesor> findByEmailAndParolaAndRol(String email, String parola, String rol);

}
