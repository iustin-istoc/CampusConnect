package com.example.campusconnect.service;

import com.example.campusconnect.model.Nota;
import com.example.campusconnect.repository.NotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaService {
    private NotaRepository notaRepository;

    public NotaService(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }
    public List<Nota> getNoteProfesor(String profesorEmail) {
        return notaRepository.findByProfesorEmail(profesorEmail);
    }

    public void stergeNota(Long id) {
        notaRepository.deleteById(id);
    }

}
