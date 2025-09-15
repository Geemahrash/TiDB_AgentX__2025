package com.backend.AI.tidbsearch;

import com.backend.AI.model.QnAEntry;
import com.backend.AI.repository.QnARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TidbSearchService {

    private final QnARepository qnaRepository;
    
    @Autowired
    public TidbSearchService(QnARepository qnaRepository) {
        this.qnaRepository = qnaRepository;
    }

    /**
     * Search for relevant QnA entries using B+ tree index-based search.
     * 
     * This implementation uses TiDB's B+ tree indexing capabilities through JPA.
     * B+ trees are efficient for range queries and are the default index structure
     * in many SQL databases including TiDB/MySQL.
     * 
     * @param sessionId The session ID for the current conversation
     * @param prompt The user's prompt/question
     * @return Formatted string with relevant answers found in the database
     */
    public String searchRequiredData(String sessionId, String prompt) {
        // Extract keywords from the prompt
        Set<String> keywords = extractKeywords(prompt);
        
        // Use B+ tree indexed search for each keyword
        // This leverages TiDB's B+ tree indexing on the database side
        List<QnAEntry> results = new ArrayList<>();
        
        // If we have keywords, search by them
        if (!keywords.isEmpty()) {
            for (String keyword : keywords) {
                // This uses B+ tree indexes in TiDB for efficient lookups
                List<QnAEntry> keywordResults = qnaRepository.searchByQuestionOrKeywords(keyword);
                results.addAll(keywordResults);
            }
        } else {
            // Fallback: search using the whole prompt as a search term
            results = qnaRepository.searchByQuestionOrKeywords(prompt);
        }
        
        // Remove duplicates
        results = results.stream().distinct().collect(Collectors.toList());
        
        // If no results found
        if (results.isEmpty()) {
            return "No relevant information found in the database for: " + prompt;
        }
        
        // Rank results using relevance scoring
        Map<QnAEntry, Double> scoredResults = rankResults(results, prompt, keywords);
        
        // Sort by score (descending)
        List<Map.Entry<QnAEntry, Double>> sortedResults = new ArrayList<>(scoredResults.entrySet());
        sortedResults.sort(Map.Entry.<QnAEntry, Double>comparingByValue().reversed());
        
        // Format the results (limit to top 3 for readability)
        StringBuilder response = new StringBuilder();
        response.append("Found " + results.size() + " relevant entries in the database:\n\n");
        
        int count = 0;
        for (Map.Entry<QnAEntry, Double> entry : sortedResults) {
            if (count++ >= 3) break; // Limit to top 3 results
            
            QnAEntry qna = entry.getKey();
            double score = entry.getValue();
            
            response.append("Question: ").append(qna.getQuestion()).append("\n");
            response.append("Answer: ").append(qna.getAnswer()).append("\n");
            response.append("Relevance: ").append(String.format("%.2f", score)).append("\n\n");
        }
        
        return response.toString();
    }
    
    /**
     * Extract potential keywords from the prompt.
     */
    private Set<String> extractKeywords(String prompt) {
        // Simple keyword extraction - split by spaces and filter out common words
        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "is", "are", "was", "were",
            "in", "on", "at", "to", "for", "with", "by", "about", "like", "through",
            "over", "before", "between", "after", "since", "without", "under", "of"
        ));
        
        Set<String> keywords = new HashSet<>();
        String[] words = prompt.toLowerCase().split("\\s+");
        
        for (String word : words) {
            // Clean the word of punctuation
            word = word.replaceAll("[^a-zA-Z0-9]", "");
            
            // Add if not a stop word and not too short
            if (!stopWords.contains(word) && word.length() > 2) {
                keywords.add(word);
            }
        }
        
        return keywords;
    }
    
    /**
     * Rank results based on relevance to the prompt and keywords.
     * This simulates the ranking that would happen in a B+ tree traversal.
     */
    private Map<QnAEntry, Double> rankResults(List<QnAEntry> results, String prompt, Set<String> keywords) {
        Map<QnAEntry, Double> scores = new HashMap<>();
        String promptLower = prompt.toLowerCase();
        
        for (QnAEntry entry : results) {
            double score = 0.0;
            String questionLower = entry.getQuestion().toLowerCase();
            String keywordsLower = entry.getKeywords() != null ? entry.getKeywords().toLowerCase() : "";
            
            // Exact match bonus
            if (questionLower.equals(promptLower)) {
                score += 10.0;
            }
            
            // Contains whole prompt bonus
            if (questionLower.contains(promptLower)) {
                score += 5.0;
            }
            
            // Keyword matches in question
            for (String keyword : keywords) {
                if (questionLower.contains(keyword)) {
                    score += 1.0;
                }
            }
            
            // Keyword matches in keywords field
            for (String keyword : keywords) {
                if (keywordsLower.contains(keyword)) {
                    score += 0.5;
                }
            }
            
            // Length similarity (prefer questions of similar length to prompt)
            double lengthRatio = 1.0 - (Math.abs(questionLower.length() - promptLower.length()) / 
                                     (double) Math.max(questionLower.length(), promptLower.length()));
            score += lengthRatio * 2.0;
            
            scores.put(entry, score);
        }
        
        return scores;
    }
}

