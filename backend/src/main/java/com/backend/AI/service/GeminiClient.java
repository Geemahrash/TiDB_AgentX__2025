package com.backend.AI.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiClient {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=YOUR_API_KEY";

    public String generateAnswer(String prompt, String requiredData) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[] {
                    Map.of("parts", new Object[] {
                            Map.of("text", prompt + "\n\nContext:\n" + requiredData)
                    })
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    GEMINI_API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Parse response (simplified)
            Map res = response.getBody();
            if (res != null && res.containsKey("candidates")) {
                var candidates = (java.util.List<Map<String, Object>>) res.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> first = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) first.get("content");
                    java.util.List<Map<String, Object>> parts = (java.util.List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }

            return "No answer generated";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}

