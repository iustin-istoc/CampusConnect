package com.example.campusconnect.service;

import com.example.campusconnect.model.Orar;
import com.example.campusconnect.repository.OrarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrarService {
    private final OrarRepository repository;

    public OrarService(OrarRepository repository) {
        this.repository = repository;
    }

   // public List<Orar> getOrarByStudentEmail(String email) {
    //    return repository.findByStudentEmail(email);
   // }

    public Orar save(Orar orar) {
        return repository.save(orar);
    }
}
