package com.example.campusconnect.service;

import com.example.campusconnect.model.Anunt;
import com.example.campusconnect.repository.AnuntRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnuntService {

    private final AnuntRepository repo;

    public AnuntService(AnuntRepository repo) {
        this.repo = repo;
    }

    public List<Anunt> getAll() {
        return repo.findAll();
    }

    public Anunt save(Anunt anunt) {
        return repo.save(anunt);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Anunt getById(Long id) {
        return repo.findById(id).orElse(null);

    }

}
