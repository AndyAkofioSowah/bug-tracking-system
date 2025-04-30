package com.example.bugtrackingsystem;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "overseer123";
        String hashed = encoder.encode(rawPassword);
        System.out.println("Hashed password: " + hashed);
    }
}
