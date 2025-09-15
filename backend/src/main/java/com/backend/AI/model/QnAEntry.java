package com.backend.AI.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a Question and Answer pair in the database.
 */
@Entity
@Table(name = "qna_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QnAEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String question;
    
    @Column(nullable = false, length = 5000)
    private String answer;
    
    @Column(name = "keywords", length = 500)
    private String keywords;
    
    /**
     * Constructor with question and answer.
     */
    public QnAEntry(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    
    /**
     * Constructor with question, answer, and keywords.
     */
    public QnAEntry(String question, String answer, String keywords) {
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
    }
}