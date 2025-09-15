package com.backend.AI.util;

import com.backend.AI.model.QnAEntry;
import com.backend.AI.repository.QnARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to load sample data into the database on application startup.
 * This is useful for development and testing purposes.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private final QnARepository qnaRepository;

    @Autowired
    public DataLoader(QnARepository qnaRepository) {
        this.qnaRepository = qnaRepository;
    }

    @Override
    public void run(String... args) {
        // Check if we already have data
        if (qnaRepository.count() > 0) {
            logger.info("Database already contains {} QnA entries, skipping data load", qnaRepository.count());
            return;
        }

        logger.info("Loading sample QnA data into the database...");
        loadSampleData();
    }

    /**
     * Load sample data from the CSV file
     */
    private void loadSampleData() {
        try {
            // Load the sample data file from resources
            ClassPathResource resource = new ClassPathResource("sample-qna-data.csv");
            List<QnAEntry> entries = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        // Skip header
                        continue;
                    }

                    String[] parts = line.split(",", -1); // -1 to keep trailing empty values

                    if (parts.length >= 2) {
                        String question = parts[0].trim();
                        String answer = parts[1].trim();
                        String keywords = parts.length > 2 ? parts[2].trim() : "";

                        QnAEntry entry = new QnAEntry(question, answer, keywords);
                        entries.add(entry);
                    }
                }
            }

            // Save all entries to the database
            qnaRepository.saveAll(entries);
            logger.info("Successfully loaded {} QnA entries into the database", entries.size());

        } catch (Exception e) {
            logger.error("Error loading sample data: {}", e.getMessage(), e);
        }
    }
}