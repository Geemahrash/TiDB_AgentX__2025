package com.backend.AI.controller;

import com.backend.AI.model.QnAEntry;
import com.backend.AI.repository.QnARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qna")
public class QnAController {

    private final QnARepository qnaRepository;

    @Autowired
    public QnAController(QnARepository qnaRepository) {
        this.qnaRepository = qnaRepository;
    }

    /**
     * Get all QnA entries
     */
    @GetMapping
    public List<QnAEntry> getAllEntries() {
        return qnaRepository.findAll();
    }

    /**
     * Get a specific QnA entry by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<QnAEntry> getEntryById(@PathVariable Long id) {
        return qnaRepository.findById(id)
                .map(entry -> ResponseEntity.ok().body(entry))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new QnA entry
     */
    @PostMapping
    public ResponseEntity<QnAEntry> createEntry(@RequestBody QnAEntry entry) {
        QnAEntry savedEntry = qnaRepository.save(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
    }

    /**
     * Update an existing QnA entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<QnAEntry> updateEntry(@PathVariable Long id, @RequestBody QnAEntry entry) {
        return qnaRepository.findById(id)
                .map(existingEntry -> {
                    existingEntry.setQuestion(entry.getQuestion());
                    existingEntry.setAnswer(entry.getAnswer());
                    existingEntry.setKeywords(entry.getKeywords());
                    QnAEntry updatedEntry = qnaRepository.save(existingEntry);
                    return ResponseEntity.ok().body(updatedEntry);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a QnA entry
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        return qnaRepository.findById(id)
                .map(entry -> {
                    qnaRepository.delete(entry);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search QnA entries by keyword
     */
    @GetMapping("/search")
    public List<QnAEntry> searchEntries(@RequestParam String query) {
        return qnaRepository.searchByQuestionOrKeywords(query);
    }

    /**
     * Import QnA entries from a CSV file
     * Format: question,answer,keywords
     */
    @PostMapping("/import/csv")
    public ResponseEntity<Map<String, Object>> importFromCsv(@RequestParam("file") MultipartFile file) {
        List<QnAEntry> importedEntries = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            // Skip header if present
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    // Check if this is a header line
                    if (line.toLowerCase().contains("question") && line.toLowerCase().contains("answer")) {
                        continue;
                    }
                }
                
                try {
                    String[] parts = line.split(",", -1); // -1 to keep trailing empty values
                    
                    if (parts.length >= 2) {
                        String question = parts[0].trim();
                        String answer = parts[1].trim();
                        String keywords = parts.length > 2 ? parts[2].trim() : "";
                        
                        QnAEntry entry = new QnAEntry(question, answer, keywords);
                        importedEntries.add(entry);
                        successCount++;
                    } else {
                        errorCount++;
                    }
                } catch (Exception e) {
                    errorCount++;
                }
            }
            
            // Save all valid entries
            qnaRepository.saveAll(importedEntries);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "totalImported", successCount,
                "errors", errorCount,
                "message", "Imported " + successCount + " entries with " + errorCount + " errors."
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error importing CSV: " + e.getMessage()
            ));
        }
    }

    /**
     * Import QnA entries from JSON
     */
    @PostMapping("/import/json")
    public ResponseEntity<Map<String, Object>> importFromJson(@RequestBody List<QnAEntry> entries) {
        try {
            List<QnAEntry> savedEntries = qnaRepository.saveAll(entries);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "totalImported", savedEntries.size(),
                "message", "Successfully imported " + savedEntries.size() + " entries."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error importing JSON: " + e.getMessage()
            ));
        }
    }
}