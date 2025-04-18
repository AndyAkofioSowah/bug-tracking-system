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

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;


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

}

