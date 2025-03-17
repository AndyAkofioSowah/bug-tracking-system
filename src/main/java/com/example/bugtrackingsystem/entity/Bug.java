package com.example.bugtrackingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String priority;
    private String status = "Open"; // Default status
    private String filePath; // Stores image OR video filename

    public Bug() {}

    public Bug(String title, String description, String priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
}

