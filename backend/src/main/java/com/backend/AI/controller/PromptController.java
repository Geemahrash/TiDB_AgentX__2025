package com.backend.AI.controller;

import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class PromptController {

    @PostMapping("/save")
    public String savePrompt(@RequestBody String prompt) {
        try (FileWriter writer = new FileWriter("prompt.txt",true)){
            writer.write(prompt + System.lineSeparator());
            return "Prompt saved successfully!";
        }catch (IOException e){
            return "error saving prompt : " + e.getMessage();
        }
    }
}

