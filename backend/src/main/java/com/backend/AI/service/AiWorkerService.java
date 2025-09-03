package com.backend.AI.service;

import com.backend.AI.storage.PromptData;
import com.backend.AI.storage.PromptStorage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiWorkerService {
    private static final Logger logger = LoggerFactory.getLogger(AiWorkerService.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Runs every 2 seconds.
     * Iterates over all sessions and their prompt objects,
     * picks one ready for processing, and sends it to Gemini.
     */
    @Scheduled(fixedRate = 2000)
    public void processPrompts() {
        // Get all sessions and their prompts
        Map<String, List<PromptData>> allSessions = PromptStorage.getAllPromptObjects();

        for (Map.Entry<String, List<PromptData>> entry : allSessions.entrySet()) {
            String sessionId = entry.getKey();
            List<PromptData> objects = entry.getValue();

            for (PromptData obj : objects) {
                if (obj.isReadyForProcessing()) {
                    obj.setProcessing(true);

                    logger.info("Processing prompt for session {}: {}", sessionId, obj.getPrompt());

                    // Build input for Gemini
                    String combinedInput = obj.getPrompt();
                    if (obj.getRequiredData() != null && !obj.getRequiredData().isBlank()) {
                        combinedInput += "\n\nAdditional Context:\n" + obj.getRequiredData();
                    } else {
                        combinedInput += "\n\n(Note: No external data provided. Use your own knowledge fully.)";
                    }

                    // Call Gemini API
                    String aiAnswer = callGeminiApi(combinedInput);

                    // Save answer back into the object
                    obj.setAnswer(aiAnswer);
                    obj.setProcessing(false);

                    logger.info("Finished processing for session {}. Answer = {}", sessionId, obj.getAnswer());
                }
            }
        }
    }

    /**
     * Actual Gemini API call.
     */
    private String callGeminiApi(String input) {
        try {
            String url = geminiApiUrl + geminiApiKey;

            // Build JSON payload for Gemini
            String body = """
        {
          "contents": [
            {
              "parts": [
                {"text": "%s"}
              ]
            }
          ]
        }
        """.formatted(input.replace("\"", "\\\""));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String respBody = response.getBody();
                if (respBody == null || respBody.isBlank()) return "No answer returned";

                // parse JSON and extract the text field
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(respBody);

                JsonNode textNode = root.path("candidates")
                        .path(0)
                        .path("content")
                        .path("parts")
                        .path(0)
                        .path("text");

                if (textNode.isMissingNode() || textNode.isNull()) {
                    logger.warn("Gemini response did not contain expected text path; returning raw body");
                    return respBody;
                }

                return textNode.asText("");
            } else {
                logger.error("Gemini API returned non-2xx status {} : {}", response.getStatusCode(), response.getBody());
                return "Error: Gemini returned status " + response.getStatusCode();
            }
        } catch (Exception e) {
            logger.error("Error calling Gemini API", e);
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
