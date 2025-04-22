package com.example.bugtrackingsystem.repository;

import com.example.bugtrackingsystem.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {}
