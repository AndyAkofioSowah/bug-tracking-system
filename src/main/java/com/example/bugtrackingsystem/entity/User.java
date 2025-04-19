package com.example.bugtrackingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Setter
    @Column(unique = true)
    private String username;

    @Setter
    private String password;

    @Setter
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "submittedBy", fetch = FetchType.EAGER)
    private List<Bug> submittedBugs;




    @Setter
    private String role; // Should be "USER" or "ADMIN"

    public User() {}

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }


}

