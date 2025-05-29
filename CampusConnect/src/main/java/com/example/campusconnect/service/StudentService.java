package com.example.campusconnect.service;

import com.example.campusconnect.model.Student;
import com.example.campusconnect.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    public List<Student> getAll() {
        return repo.findAll();
    }

    public Student save(Student s) {
        return repo.save(s);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Student getByEmail(String email) {
        Student student = repo.findByEmail(email);
        if (student == null) {
            throw new RuntimeException("Student not found");
        }
        return student;
    }

}
