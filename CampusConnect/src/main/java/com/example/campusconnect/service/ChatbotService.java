package com.example.campusconnect.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ChatbotService {

    // schimbi modelul aici (exact cum îl ai în Ollama)
    private final String ollamaModel = "llama3";

    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:11434") // Ollama default
            .build();

    public String ask(String userMessage) {

        // Ollama /api/generate (simplu)
        Map<String, Object> payload = Map.of(
                "model", ollamaModel,
                "prompt", userMessage,
                "stream", false
        );

        Map<?, ?> resp = client.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || !resp.containsKey("response")) return "Nu am primit răspuns de la model.";
        return String.valueOf(resp.get("response"));
    }
}