package com.backend.AI.controller;

import com.backend.AI.entity.Document;
import com.backend.AI.tidbsearch.TidbSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tidb")
public class TidbSearchController {

    @Autowired
    private TidbSearchService tidbSearchService;

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam String sessionId, @RequestParam String prompt) {
        String result = tidbSearchService.searchRequiredData(sessionId, prompt);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/connection-info")
    public ResponseEntity<Map<String, String>> getConnectionInfo() {
        Map<String, String> connectionInfo = tidbSearchService.getConnectionInfo();
        return ResponseEntity.ok(connectionInfo);
    }

    @PostMapping("/documents")
    public ResponseEntity<Document> createDocument(@RequestBody DocumentRequest request) {
        // In a real implementation, you would convert the text to a vector here
        // For now, we'll use a placeholder empty byte array
        byte[] contentVector = new byte[0]; // Placeholder
        
        Document document = tidbSearchService.storeDocument(
                request.getTitle(),
                request.getContent(),
                contentVector,
                request.getDocumentType());
        
        return ResponseEntity.ok(document);
    }

    @GetMapping("/documents/text-search")
    public ResponseEntity<List<Document>> searchByText(@RequestParam String searchText) {
        List<Document> documents = tidbSearchService.searchByText(searchText);
        return ResponseEntity.ok(documents);
    }
}