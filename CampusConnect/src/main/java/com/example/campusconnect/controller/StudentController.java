package com.example.campusconnect.controller;

import com.example.campusconnect.model.Student;
import com.example.campusconnect.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")

public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Student save(@RequestBody Student student) {
        return service.save(student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{email}")
    public Student getByEmail(@PathVariable String email) {
        return service.getByEmail(email);
    }

}
