package com.example.bugtrackingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter
    @Column(nullable = false, unique = true)
    private String companyEmail;

    @Setter
    private String companyName;

    @Setter
    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private User admin;


    public Company() {}

    public Company(String companyName, User admin) {
        this.companyName = companyName;
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }


    public String getCompanyName() {
        return companyName;
    }

    public User getAdmin() {
        return admin;
    }


    public String getCompanyEmail() { return companyEmail; }



}

