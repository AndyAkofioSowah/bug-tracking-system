package com.example.bugtrackingsystem.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "users") // Ensures table name is 'users'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true)
    private String username;

    @Setter
    private String password;

    @Setter
    private String role; // Should be "USER" or "ADMIN"

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

}

