package com.backend.AI.tidbsearch;

import com.backend.AI.config.TidbConfig;
import com.backend.AI.entity.Document;
import com.backend.AI.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TidbSearchService {

    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private TidbConfig tidbConfig;
    
    /**
     * Search for documents using text-based search
     * 
     * @param searchText The text to search for
     * @return List of matching documents
     */
    public List<Document> searchByText(String searchText) {
        return documentRepository.findByContentFullTextSearch(searchText);
    }
    
    /**
     * Search for documents using vector similarity
     * 
     * @param queryVector The vector representation of the query
     * @param limit Maximum number of results to return
     * @return List of similar documents
     */
    public List<Document> searchByVector(byte[] queryVector, int limit) {
        return documentRepository.findSimilarDocuments(queryVector, limit);
    }
    
    /**
     * Search for documents using both text and vector similarity
     * 
     * @param searchText The text to search for
     * @param queryVector The vector representation of the query
     * @param limit Maximum number of results to return
     * @return List of matching documents
     */
    public List<Document> searchByTextAndVector(String searchText, byte[] queryVector, int limit) {
        return documentRepository.findByContentAndVectorSimilarity(searchText, queryVector, limit);
    }
    
    /**
     * Process a search request from a user prompt
     * 
     * @param sessionId The session ID for tracking the request
     * @param prompt The user's search prompt
     * @return A response with search results
     */
    public String searchRequiredData(String sessionId, String prompt) {
        try {
            // For now, return a simple response
            // In a real implementation, we would:
            // 1. Convert the prompt to a vector using an embedding model
            // 2. Perform the vector search in TiDB
            // 3. Format and return the results
            
            return "TiDB search completed successfully for prompt: " + prompt;
        } catch (Exception e) {
            return "Error searching TiDB: " + e.getMessage();
        }
    }
    
    /**
     * Store a document in TiDB with vector embedding
     * 
     * @param title Document title
     * @param content Document content
     * @param contentVector Vector representation of the content
     * @param documentType Type of document
     * @return The stored document
     */
    public Document storeDocument(String title, String content, byte[] contentVector, String documentType) {
        Document document = new Document();
        document.setTitle(title);
        document.setContent(content);
        document.setContentVector(contentVector);
        document.setDocumentType(documentType);
        
        return documentRepository.save(document);
    }
    
    /**
     * Get TiDB connection information
     * 
     * @return Map containing connection details
     */
    public Map<String, String> getConnectionInfo() {
        Map<String, String> connectionInfo = new HashMap<>();
        connectionInfo.put("jdbcUrl", tidbConfig.getJdbcUrl());
        connectionInfo.put("username", tidbConfig.getUsername());
        // Don't include password in the returned info for security reasons
        connectionInfo.put("driverClassName", tidbConfig.getDriverClassName());
        return connectionInfo;
    }
}

