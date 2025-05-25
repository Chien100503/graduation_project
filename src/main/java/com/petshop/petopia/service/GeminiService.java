package com.petshop.petopia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private String hardcodedPrompt;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.promt}")
    private String promptFilePath;

    public GeminiService(RestTemplate restTemplate, ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        this.hardcodedPrompt = loadHardcodedPrompt();
    }

    private String loadHardcodedPrompt() {
        Resource resource = resourceLoader.getResource(promptFilePath);
        try {
            if (!resource.exists()) {
                throw new IOException("Prompt file not found: " + promptFilePath);
            }
            return new String(Files.readAllBytes(resource.getFile().toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            // Ném RuntimeException để Spring context không khởi tạo nếu file không đọc được
            throw new RuntimeException("Failed to load hardcoded prompt from file: " + promptFilePath, e);
        }
    }

    public String generateContent(String userPrompt) {
        if (this.hardcodedPrompt == null || this.hardcodedPrompt.isEmpty()) {
            System.err.println("Warning: Hardcoded prompt not loaded or empty.");
            return "Lỗi nội bộ: Prompt mặc định chưa được tải.";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = createGeminiRequestBody(userPrompt);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String apiUrlWithKey = geminiApiUrl.replace("{{GEMINI_API_KEY}}", geminiApiKey);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiUrlWithKey,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                JsonNode root = objectMapper.readTree(responseEntity.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    JsonNode firstCandidate = candidates.get(0);
                    JsonNode contentNode = firstCandidate.path("content");
                    JsonNode partsNode = contentNode.path("parts");
                    if (partsNode.isArray() && !partsNode.isEmpty()) {
                        JsonNode firstPart = partsNode.get(0);
                        JsonNode textNode = firstPart.path("text");
                        return textNode.asText();
                    }
                }
            }
            return "Không nhận được phản hồi hợp lệ từ Gemini.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Lỗi khi đọc phản hồi từ Gemini: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi gọi API Gemini: " + e.getMessage();
        }
    }

    private Map<String, Object> createGeminiRequestBody(String userPrompt) {
        Map<String, Object> contentPart1 = new HashMap<>();
        contentPart1.put("text", hardcodedPrompt);

        Map<String, Object> contentPart2 = new HashMap<>();
        contentPart2.put("text", userPrompt);

        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(contentPart1);
        parts.add(contentPart2);

        Map<String, Object> content = new HashMap<>();
        content.put("role", "user");
        content.put("parts", parts);

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("maxOutputTokens", 200);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }
}