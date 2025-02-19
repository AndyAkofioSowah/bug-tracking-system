package com.example.bugtrackingsystem.repository;

import com.example.bugtrackingsystem.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    boolean existsByTitleIgnoreCaseAndDescriptionIgnoreCase(String title, String description);
    long countByStatus(String status);


    // Search for bugs by title or description (case-insensitive)
    List<Bug> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}


