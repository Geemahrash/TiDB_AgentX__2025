package com.backend.AI.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.AI.storage.PromptData;
import com.backend.AI.storage.PromptStorage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AiWorkerService {
    private static final Logger logger = LoggerFactory.getLogger(AiWorkerService.class);


    /**
     * Runs every 2 seconds.
     * Iterates over all sessions and their prompt objects,
     * picks one ready for processing, and sends it to Gemini (stubbed for now).
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

                    String combinedInput = obj.getPrompt();
                    if (obj.getRequiredData() != null && !obj.getRequiredData().isBlank()) {
                        combinedInput += "\n\nAdditional Context:\n" + obj.getRequiredData();
                    } else {
                        combinedInput += "\n\n(Note: No external data provided. Use your own knowledge fully.)";
                    }

                    String aiAnswer = callGeminiApi(combinedInput);

                    obj.setAnswer(aiAnswer);
                    obj.setProcessing(false);

                    logger.info("Finished processing for session {}. Answer = {}", sessionId, obj.getAnswer());
                }
            }

        }
    }

    /**
     * Stub for Gemini API call.
     * Replace with actual integration later.
     */
    private String callGeminiApi(String input) {
        // TODO: Replace with Gemini API call
        return "AI response for input: " + input;
    }
}
