package com.example.bugtrackingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter

@Entity
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedAt;

    private String title;
    private String description;
    private String priority;
    private String status = "Open"; // Default status
    private String filePath; // Stores image OR video filename

    @Getter
    @Column(name = "category")
    private String category;


    public Bug() {}

    public Bug(String title, String description, String priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public Bug(String title, String description, String priority, String status, String category) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.category = category;
    }

    // Automatically set the reported time before persisting
    @PrePersist
    public void setReportedAt() {
        this.reportedAt = LocalDateTime.now();
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }
}

