package com.example.bugtrackingsystem.repository;


import com.example.bugtrackingsystem.entity.Company;
import com.example.bugtrackingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByCompanyName(String companyName);
    //  Matches the field name in the Company entity

    List<Company> findAll();

    Company findByCompanyEmail(String email);


    Company findByAdmin(User admin);

}