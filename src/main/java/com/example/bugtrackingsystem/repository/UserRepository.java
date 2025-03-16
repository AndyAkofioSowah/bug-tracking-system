package com.example.bugtrackingsystem.repository;

import com.example.bugtrackingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//This interface defines a Spring Data JPA repository for managing User entities in the database.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
