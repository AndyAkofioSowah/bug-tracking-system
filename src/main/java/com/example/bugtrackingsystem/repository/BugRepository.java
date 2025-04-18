package com.example.bugtrackingsystem.repository;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    List<Bug> findByStatusIgnoreCase(String status);




    boolean existsByTitleIgnoreCaseAndDescriptionIgnoreCase(String title, String description);

    long countByStatus(String status);

    List<Bug> findByCompany(Company company);



    List<Bug> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCompany_CompanyNameContainingIgnoreCase(String title, String description, String companyName);

}

