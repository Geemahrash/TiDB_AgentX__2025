package com.backend.AI.controller;

import com.backend.AI.entity.PhysicsChapter;
import com.backend.AI.repository.PhysicsChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/physics")
public class PhysicsChapterController {

    @Autowired
    private PhysicsChapterRepository physicsChapterRepository;

    @GetMapping("/chapters")
    public ResponseEntity<List<PhysicsChapter>> getAllChapters() {
        List<PhysicsChapter> chapters = physicsChapterRepository.findAll();
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/chapters/{chapterNo}")
    public ResponseEntity<PhysicsChapter> getChapterById(@PathVariable Integer chapterNo) {
        return physicsChapterRepository.findById(chapterNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chapters/search")
    public ResponseEntity<List<PhysicsChapter>> searchChapters(@RequestParam(required = false) String title,
                                                           @RequestParam(required = false) String summary) {
        if (title != null && !title.isEmpty()) {
            return ResponseEntity.ok(physicsChapterRepository.findByTitleContainingIgnoreCase(title));
        } else if (summary != null && !summary.isEmpty()) {
            return ResponseEntity.ok(physicsChapterRepository.findBySummaryContainingIgnoreCase(summary));
        } else {
            return ResponseEntity.ok(physicsChapterRepository.findAll());
        }
    }

    @GetMapping("/verify-connection")
    public ResponseEntity<Map<String, Object>> verifyConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long count = physicsChapterRepository.count();
            List<PhysicsChapter> sampleChapters = physicsChapterRepository.findAll().subList(0, Math.min(3, (int)count));
            
            response.put("success", true);
            response.put("message", "Successfully connected to TiDB and fetched data");
            response.put("totalChapters", count);
            response.put("sampleChapters", sampleChapters);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to connect to TiDB: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}