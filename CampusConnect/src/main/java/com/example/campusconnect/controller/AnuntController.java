package com.example.campusconnect.controller;

import com.example.campusconnect.model.Anunt;
import com.example.campusconnect.service.AnuntService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import com.example.campusconnect.model.AnuntDTO;
import com.example.campusconnect.model.Profesor;
import com.example.campusconnect.repository.ProfesorRepository;

@RestController
@RequestMapping("/api/anunturi")

public class AnuntController {

    private final AnuntService service;
    private final ProfesorRepository profesorRepository;

    public AnuntController(AnuntService service, ProfesorRepository profesorRepository) {
        this.service = service;
        this.profesorRepository = profesorRepository;
    }

    @GetMapping
    public List<AnuntDTO> getAll() {
        List<Anunt> anunturi = service.getAll();
        return anunturi.stream().map(anunt -> {
            AnuntDTO dto = new AnuntDTO();
            dto.setId(anunt.getId());
            dto.setTitlu(anunt.getTitlu());
            dto.setContinut(anunt.getContinut());
            dto.setData(anunt.getData());
            dto.setProfesorEmail(anunt.getProfesorEmail());

            Profesor prof = profesorRepository.findByEmail(anunt.getProfesorEmail());
            if (prof != null) {
                dto.setProfesorNume(prof.getNume());
            } else {
                dto.setProfesorNume("Profesor necunoscut");
            }

            return dto;
        }).toList();
    }

    @PostMapping
    public Anunt save(@RequestBody Anunt anunt, HttpServletRequest request) {
        String emailDinToken = (String) request.getAttribute("email");

        Profesor prof = profesorRepository.findByEmail(emailDinToken); // ← trebuie să ai injectat repository-ul

        anunt.setProfesorEmail(emailDinToken);
        anunt.setProfesorNume(prof.getNume()); // ← adaugă asta!

        return service.save(anunt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String emailDinToken = (String) request.getAttribute("email");
        Anunt anunt = service.getById(id);

        if (anunt == null || !anunt.getProfesorEmail().equals(emailDinToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public Anunt update(@PathVariable Long id, @RequestBody Anunt a) {
        Anunt vechi = service.getById(id);
        vechi.setTitlu(a.getTitlu());
        vechi.setContinut(a.getContinut());
        return service.save(vechi);
    }

}
