package com.example.bugtrackingsystem.service;


import com.example.bugtrackingsystem.entity.Company;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }


    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }



    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company findByCompanyName(String name) {
        return companyRepository.findByCompanyName(name);
    }

    public Company getCompanyByAdmin(User admin) {
        return companyRepository.findByAdmin(admin);
    }



    public Company getCompanyByEmail(String email) {
        return companyRepository.findByCompanyEmail(email);
    }


}
