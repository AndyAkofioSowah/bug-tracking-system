package com.example.bugtrackingsystem.controller;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.UserRepository;
import com.example.bugtrackingsystem.utils.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // You were missing this import for model!
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  @RequestParam String role,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Validate password strength
        if (!PasswordValidator.isValid(password)) {
            model.addAttribute("errorMessage", "Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.");
            return "register";
        }

        // Confirm passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "register";
        }

        // Check if username already exists
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("errorMessage", "Username already exists.");
            return "register";
        }

        // Save new user
        String hashedPassword = passwordEncoder.encode(password);
        System.out.println("Saving user: " + username + " with encoded password: " + hashedPassword + " and role: " + role);
        userRepository.save(new User(username, hashedPassword, role));

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/login";
    }
}

