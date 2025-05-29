package com.example.campusconnect.model;

public class AnuntDTO {
    private Long id;
    private String titlu;
    private String continut;
    private String data;
    private String profesorEmail;
    private String profesorNume;




    public Long getId() {
        return id;
    }
    public void setId(Long id) {this.id = id;}

    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }

    public String getContinut() { return continut; }
    public void setContinut(String continut) { this.continut = continut; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getProfesorEmail() { return profesorEmail; }
    public void setProfesorEmail(String profesorEmail) { this.profesorEmail = profesorEmail; }

    public String getProfesorNume() { return profesorNume; }
    public void setProfesorNume(String profesorNume) { this.profesorNume = profesorNume; }
}
