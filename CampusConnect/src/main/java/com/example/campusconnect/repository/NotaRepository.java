package com.example.campusconnect.repository;

import com.example.campusconnect.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByStudentEmail(String studentEmail);
    List<Nota> findByProfesorEmail(String profesorEmail);

}
