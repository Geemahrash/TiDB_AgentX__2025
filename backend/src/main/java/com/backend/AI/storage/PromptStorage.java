package com.backend.AI.storage;

import java.util.*;

public class PromptStorage {
    // Map to store prompts separately per sessionId
    private static Map<String, List<String>> sessionPrompts = new HashMap<>();

    public static synchronized void addPrompt(String sessionId, String prompt) {
        sessionPrompts.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(prompt);
    }

    public static synchronized List<String> getPrompts(String sessionId) {
        return sessionPrompts.getOrDefault(sessionId, new ArrayList<>());
    }
}
