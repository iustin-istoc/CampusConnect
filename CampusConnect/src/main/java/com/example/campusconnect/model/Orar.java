package com.example.campusconnect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Orar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profesor profesor;

    @ManyToOne
    private Materie materie;

    private String zi;
    private String oraInceput;
    private String oraSfarsit;
    private int an;
    private int grupa;

    private String materieNume;
    private String profesorNume;
}
