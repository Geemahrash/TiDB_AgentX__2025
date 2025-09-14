package com.backend.AI.repository;

import com.backend.AI.entity.PhysicsChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicsChapterRepository extends JpaRepository<PhysicsChapter, Integer> {
    
    // Find chapters by title containing the given text
    List<PhysicsChapter> findByTitleContainingIgnoreCase(String title);
    
    // Find chapters by summary containing the given text
    List<PhysicsChapter> findBySummaryContainingIgnoreCase(String summary);
}