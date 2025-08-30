package com.backend.AI.controller;

import com.backend.AI.storage.PromptStorage;
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

}
