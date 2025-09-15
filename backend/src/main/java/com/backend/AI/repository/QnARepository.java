package com.backend.AI.repository;

import com.backend.AI.model.QnAEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for QnAEntry entities with custom search methods.
 */
@Repository
public interface QnARepository extends JpaRepository<QnAEntry, Long> {
    
    /**
     * Find entries where the question contains the given text (case insensitive).
     */
    List<QnAEntry> findByQuestionContainingIgnoreCase(String questionText);
    
    /**
     * Find entries where the keywords contain the given text (case insensitive).
     */
    List<QnAEntry> findByKeywordsContainingIgnoreCase(String keywordText);
    
    /**
     * Custom query to search in both question and keywords fields.
     */
    @Query("SELECT q FROM QnAEntry q WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
           + "LOWER(q.keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<QnAEntry> searchByQuestionOrKeywords(@Param("searchTerm") String searchTerm);
}