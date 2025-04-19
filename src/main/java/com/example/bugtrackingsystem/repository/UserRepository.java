package com.example.bugtrackingsystem.repository;

import com.example.bugtrackingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//This interface defines a Spring Data JPA repository for managing User entities in the database.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.submittedBugs WHERE u.email = :email")
    User findByEmailWithBugs(@Param("email") String email);

    User findByUsername(String username); //

}
