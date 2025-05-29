package com.example.campusconnect.repository;

import com.example.campusconnect.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmailAndParola(String email, String parola);
    boolean existsByEmail(String email);

    Student findByEmail(String email);

}
