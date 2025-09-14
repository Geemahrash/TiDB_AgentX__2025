package com.backend.AI.repository;

import com.backend.AI.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    // Find documents by title containing the given text
    List<Document> findByTitleContainingIgnoreCase(String title);
    
    // Find documents by content containing the given text (full-text search)
    @Query(value = "SELECT * FROM documents WHERE MATCH(content) AGAINST(:searchText IN NATURAL LANGUAGE MODE)", 
           nativeQuery = true)
    List<Document> findByContentFullTextSearch(@Param("searchText") String searchText);
    
    // Vector similarity search using TiDB's vector functions
    // This uses cosine similarity between the document vector and the query vector
    @Query(value = "SELECT *, COSINE_SIMILARITY(content_vector, :queryVector) AS similarity " +
                  "FROM documents " +
                  "ORDER BY similarity DESC " +
                  "LIMIT :limit", 
           nativeQuery = true)
    List<Document> findSimilarDocuments(@Param("queryVector") byte[] queryVector, @Param("limit") int limit);
    
    // Combined search using both full-text and vector similarity
    @Query(value = "SELECT *, COSINE_SIMILARITY(content_vector, :queryVector) AS similarity " +
                  "FROM documents " +
                  "WHERE MATCH(content) AGAINST(:searchText IN NATURAL LANGUAGE MODE) " +
                  "ORDER BY similarity DESC " +
                  "LIMIT :limit", 
           nativeQuery = true)
    List<Document> findByContentAndVectorSimilarity(
            @Param("searchText") String searchText,
            @Param("queryVector") byte[] queryVector, 
            @Param("limit") int limit);
}