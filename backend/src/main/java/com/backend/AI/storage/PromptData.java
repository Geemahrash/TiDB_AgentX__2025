package com.backend.AI.storage;

public class PromptData {
    private String prompt;
    private String answer;
    private String requiredData;

    public PromptData(String prompt) {
        this.prompt = prompt;
        this.answer = "";
        this.requiredData = "";
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getRequiredData() { return requiredData; }
    public void setRequiredData(String requiredData) { this.requiredData = requiredData; }
}
