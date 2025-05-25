package com.petshop.petopia.controller.user;

import com.petshop.petopia.service.GeminiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatbotController {
    private final GeminiService geminiService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chatWithPetAssistant(@RequestBody Map<String, String> request) {
        String userPrompt = request.get("text");
        if (userPrompt != null) {
            String geminiResponseText = geminiService.generateContent(userPrompt);
            Map<String, String> response = Map.of("response", geminiResponseText);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng cung cấp nội dung tin nhắn."));
        }
    }
}