package com.example.campusconnect.controller;

import com.example.campusconnect.model.Orar;
import com.example.campusconnect.model.OrarDto;
import com.example.campusconnect.model.Profesor;
import com.example.campusconnect.model.Student;
import com.example.campusconnect.repository.OrarRepository;
import com.example.campusconnect.repository.ProfesorRepository;
import com.example.campusconnect.repository.StudentRepository;
import com.example.campusconnect.service.OrarService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orar")

public class OrarController {
    private final OrarService service;
    private final ProfesorRepository profesorRepository;
    private final OrarRepository orarRepository;
    private final StudentRepository studentRepository;

    public OrarController(OrarService service,
                          ProfesorRepository profesorRepository,
                          OrarRepository orarRepository,
                          StudentRepository studentRepository) {
        this.service = service;
        this.profesorRepository = profesorRepository;
        this.orarRepository = orarRepository;
        this.studentRepository = studentRepository;
    }



    @PostMapping
    public ResponseEntity<?> save(@RequestBody OrarDto dto, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Profesor prof = profesorRepository.findByEmail(email);

        if (!prof.getMaterie().getId().equals(dto.materieId) ||
                !prof.getSpecializare().getId().equals(dto.specializareId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nu poți adăuga altă materie sau specializare.");
        }

        boolean conflict = orarRepository.existsByZiAndOraInceputAndOraSfarsitAndAnAndGrupa(
                dto.zi, dto.oraInceput, dto.oraSfarsit, dto.an, dto.grupa);

        if (conflict) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Slotul este deja ocupat.");
        }

        Orar orar = new Orar();
        orar.setZi(dto.zi);
        orar.setOraInceput(dto.oraInceput);
        orar.setOraSfarsit(dto.oraSfarsit);
        orar.setAn(dto.an);
        orar.setGrupa(dto.grupa);
        orar.setProfesor(prof);
        orar.setMaterie(prof.getMaterie());
        orar.setMaterieNume(prof.getMaterie().getNume());
        orar.setProfesorNume(prof.getNume());

        return ResponseEntity.ok(service.save(orar));
    }


    @GetMapping("/profesor")
    public ResponseEntity<?> getOreProfesor(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Profesor profesor = profesorRepository.findByEmail(email);
        if (profesor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profesorul nu a fost găsit");
        }
        return ResponseEntity.ok(orarRepository.findByProfesorId(profesor.getId()));
    }

    @GetMapping("/student")
    public List<Orar> getOrarForStudent(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Student student = studentRepository.findByEmail(email);

        return orarRepository.findByAnAndGrupa(student.getAn(), student.getGrupa());
    }

    @DeleteMapping("/{id}")
    public void deleteOra(@PathVariable Long id) {
        orarRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Orar actualizeazaOra(@PathVariable Long id, @RequestBody OrarDto dto, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        Profesor prof = profesorRepository.findByEmail(email);
        Orar orar = orarRepository.findById(id).orElseThrow();

        // validări identitate profesor + materie + specializare
        if (!prof.getId().equals(orar.getProfesor().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nu poți edita o oră a altui profesor");
        }

        orar.setZi(dto.zi);
        orar.setOraInceput(dto.oraInceput);
        orar.setOraSfarsit(dto.oraSfarsit);
        orar.setAn(dto.an);
        orar.setGrupa(dto.grupa);
        return orarRepository.save(orar);
    }

}
