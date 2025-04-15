package com.example.bugtrackingsystem.utils;

public class PasswordValidator {

    public static boolean isValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Must contain at least one uppercase, one lowercase, one digit, and one special character
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&^#()\\[\\]{}<>]).{8,}$");
    }
}
