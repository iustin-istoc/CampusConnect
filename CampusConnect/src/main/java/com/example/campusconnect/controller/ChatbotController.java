package com.example.campusconnect.controller;

import com.example.campusconnect.model.ChatRequest;
import com.example.campusconnect.model.ChatResponse;
import com.example.campusconnect.service.ChatAnswerService;
import com.example.campusconnect.service.ChatbotService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatbotController {

    private final ChatAnswerService chatAnswerService;

    public ChatbotController(ChatAnswerService chatAnswerService) {
        this.chatAnswerService = chatAnswerService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req, HttpServletRequest request) {

        String email = (String) request.getAttribute("email");
        if (email == null) {
            return new ChatResponse("Nu ești autentificat.");
        }

        String reply = chatAnswerService.answer(email, req.getMessage());
        return new ChatResponse(reply);
    }
}
