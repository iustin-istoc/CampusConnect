package com.example.campusconnect.service;

import com.example.campusconnect.model.Profesor;
import com.example.campusconnect.repository.ProfesorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfesorService {

    private final ProfesorRepository repository;

    public ProfesorService(ProfesorRepository repository) {
        this.repository = repository;
    }

    public List<Profesor> getAll() {
        return repository.findAll();
    }

    public Profesor save(Profesor profesor) {
        return repository.save(profesor);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Profesor login(String email, String parola) {
        return repository.findByEmailAndParola(email, parola).orElse(null);
    }

    public Profesor getByEmail(String email) {
        Profesor profesor = repository.findByEmail(email);
        if (profesor == null) {
            throw new RuntimeException("Profesor not found");
        }
        return profesor;
    }

}
