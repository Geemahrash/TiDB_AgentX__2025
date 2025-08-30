package com.backend.AI.storage;

import java.util.*;

public class PromptStorage {
    // Existing string-based storage
    private static Map<String, List<String>> sessionPrompts = new HashMap<>();

    // NEW: Object-based storage
    private static Map<String, List<PromptData>> sessionPromptObjects = new HashMap<>();

    // Add prompt: updates BOTH list of strings and list of objects
    public static synchronized void addPrompt(String sessionId, String prompt) {
        // Store in string list (old way)
        sessionPrompts
                .computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(prompt);

        // Store in object list (new way)
        sessionPromptObjects
                .computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new PromptData(prompt));
    }

    // Get only string list (for backward compatibility)
    public static synchronized List<String> getPrompts(String sessionId) {
        return sessionPrompts.getOrDefault(sessionId, new ArrayList<>());
    }

    // NEW: Get object list
    public static synchronized List<PromptData> getPromptObjects(String sessionId) {
        return sessionPromptObjects.getOrDefault(sessionId, new ArrayList<>());
    }
}
