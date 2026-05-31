package com.example.campusconnect.service;

import com.example.campusconnect.model.Nota;
import com.example.campusconnect.repository.NotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatAnswerService {

    private final NotaRepository notaRepository;

    public ChatAnswerService(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }

    public String answer(String studentEmail, String question) {

        String q = normalize(question);

        // 🔹 CAZ 1: „Ce note am?” → toate notele
        if (q.contains("note") && !q.contains("la ")) {
            List<Nota> note = notaRepository.findByStudentEmail(studentEmail);

            if (note.isEmpty()) {
                return "Nu ai nicio notă înregistrată.";
            }

            String raspuns = note.stream()
                    .collect(Collectors.groupingBy(
                            Nota::getMaterie,
                            Collectors.mapping(
                                    n -> String.valueOf(n.getValoare()),
                                    Collectors.joining(", ")
                            )
                    ))
                    .entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining("; "));

            return "Notele tale sunt: " + raspuns + ".";
        }

        // 🔹 CAZ 2: „Ce note am la X?”
        if (q.contains("note") && q.contains("la ")) {
            String materieCautata = extractMaterie(q);

            if (materieCautata == null) {
                return "La ce materie te referi?";
            }

            List<Nota> noteStudent = notaRepository.findByStudentEmail(studentEmail);

            List<Nota> noteLaMaterie = noteStudent.stream()
                    .filter(n -> normalize(n.getMaterie()).contains(normalize(materieCautata)))
                    .toList();

            if (noteLaMaterie.isEmpty()) {
                return "Nu ai note la " + materieCautata + ".";
            }

            String valori = noteLaMaterie.stream()
                    .map(n -> String.valueOf(n.getValoare()))
                    .collect(Collectors.joining(", "));

            return "Ai notele " + valori + " la " + noteLaMaterie.get(0).getMaterie() + ".";
        }

        return "Pot răspunde la întrebări despre note (ex: «Ce note am?» sau «Ce note am la IRA?»).";
    }

    // ---------------- UTIL ----------------

    private String extractMaterie(String q) {
        int idx = q.indexOf("la ");
        if (idx < 0) return null;

        String m = q.substring(idx + 3)
                .replaceAll("[?.!]", "")
                .trim();

        return m.isEmpty() ? null : m;
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase()
                .replace("ă", "a").replace("â", "a").replace("î", "i")
                .replace("ș", "s").replace("ş", "s")
                .replace("ț", "t").replace("ţ", "t")
                .trim();
    }
}
