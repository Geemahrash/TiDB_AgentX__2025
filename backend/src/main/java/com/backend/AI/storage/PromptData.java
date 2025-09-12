package com.backend.AI.storage;

public class PromptData {
    private String prompt;
    private String answer;
    private String requiredData;
    // true while the entry is being processed by AI
    private volatile boolean processing;

    public PromptData(String prompt) {
        this.prompt = prompt;
        this.answer = "";
        this.requiredData = "";
        this.processing = false;
    }

    // Prompt
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    // Answer (AI output)
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    // Required data text to be filled by other code
    public String getRequiredData() { return requiredData; }
    public void setRequiredData(String requiredData) { this.requiredData = requiredData; }

    // Processing flag
    public boolean isProcessing() { return processing; }
    public void setProcessing(boolean processing) { this.processing = processing; }

    // Convenience: whether this entry is ready to be processed by AI
    public boolean isReadyForProcessing() {
        return prompt != null && !prompt.isBlank()
                && (answer == null || answer.isBlank())
                && !processing;
    }
    
}
