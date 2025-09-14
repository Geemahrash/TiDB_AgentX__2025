package com.backend.AI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "physics_chapters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhysicsChapter {
    
    @Id
    @Column(name = "chapter_no")
    private Integer chapterNo;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;
}