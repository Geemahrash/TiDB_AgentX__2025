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
    /**
     * Find the first prompt object for a given session that is ready to be processed,
     * mark it as 'processing', and return it.
     * Returns null if none found.
     */
    public static synchronized PromptData claimNextForProcessing(String sessionId) {
        List<PromptData> objects = sessionPromptObjects.get(sessionId);
        if (objects == null) {
            return null;
        }

        for (PromptData pd : objects) {
            if (pd.isReadyForProcessing()) {
                pd.setProcessing(true); // lock it for this worker
                return pd;
            }
        }
        return null;
    }
    // Helper: allow the worker to iterate all sessions and their PromptData lists
    public static synchronized Map<String, List<PromptData>> getAllPromptObjects() {
        return sessionPromptObjects;
    }


}
