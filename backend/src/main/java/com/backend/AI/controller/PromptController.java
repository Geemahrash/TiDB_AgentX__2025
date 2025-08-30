package com.backend.AI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromptController {

    @GetMapping("/api/hello")
    public String sayHello() {
        return "Hello from Backend!";
    }
}

