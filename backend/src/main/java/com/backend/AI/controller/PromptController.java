package com.backend.AI.controller;

import com.backend.AI.storage.PromptStorage;
import com.backend.AI.storage.PromptData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PromptController {

    @PostMapping("/prompt")
    public String savePrompt(@RequestBody PromptRequest request) {
        // Store the prompt in PromptStorage using sessionId
        PromptStorage.addPrompt(request.getSessionId(), request.getPrompt());
        return "Prompt saved successfully for session: " + request.getSessionId();
    }
    @GetMapping("/prompts/{sessionId}")
    public List<String> getPrompts(@PathVariable String sessionId) {
        return PromptStorage.getPrompts(sessionId);
    }
    @GetMapping("/getPromptObjects")
    public List<PromptData> getPromptObject(@RequestParam String sessionId) {
        return PromptStorage.getPromptObjects(sessionId);
    }
    @GetMapping("/promptObjects/{sessionId}")
    public List<PromptData> getPromptObjects(@PathVariable String sessionId) {
        return PromptStorage.getPromptObjects(sessionId);
    }
    @PutMapping("/requiredData")
    public String updateRequiredData(
            @RequestParam String sessionId,
            @RequestParam int index,
            @RequestBody String requiredData
    ) {
        var objects = PromptStorage.getPromptObjects(sessionId);
        if (index >= 0 && index < objects.size()) {
            objects.get(index).setRequiredData(requiredData);
            return "Required data updated for prompt at index " + index;
        } else {
            return "Invalid index for session: " + sessionId;
        }
    }

}
