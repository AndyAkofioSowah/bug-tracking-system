package com.example.bugtrackingsystem.controller;
import com.example.bugtrackingsystem.entity.Company;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.CompanyRepository;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class UserController {

    @Autowired
    private CompanyRepository companyRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
                                  @RequestParam(required = false) String fullName,
                                  @RequestParam(required = false) String dob,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) String established,
                                  @RequestParam String role,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  @RequestParam String email,
                                  @RequestParam(required = false) String companyEmail,
                                  @RequestParam(required = false) String companyName,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Password validation
        if (!PasswordValidator.isValid(password)) {
            model.addAttribute("errorMessage", "Password must be strong.");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "register";
        }

        // Determine login credentials based on role
        String loginEmail = role.equals("ADMIN") ? companyEmail : email;
        String displayUsername = role.equals("ADMIN") ? companyName : username;

        // Check for existing email and username
        if (userRepository.findByEmailWithBugs(loginEmail) != null) {
            model.addAttribute("errorMessage", "An account with this email already exists.");
            return "register";
        }

        if (userRepository.findByUsername(displayUsername) != null) {
            model.addAttribute("errorMessage", "This username is already taken.");
            return "register";
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(displayUsername);
        newUser.setEmail(loginEmail);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);

// Parse DOB if provided
        if (dob != null && !dob.isEmpty()) {
            try {
                Date parsedDob = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
                newUser.setDateOfBirth(parsedDob);
            } catch (ParseException e) {
                model.addAttribute("errorMessage", "Invalid date format.");
                return "register";
            }
        }


        //  Save user
        userRepository.save(newUser);
        // Only save company if role is ADMIN
        if (role.equals("ADMIN")) {
            Company company = new Company();
            company.setCompanyName(companyName); // from form input
            company.setCompanyEmail(companyEmail);
            company.setAdmin(newUser);    //  associate the company with the new admin
            companyRepository.save(company);
        }


        redirectAttributes.addFlashAttribute("successMessage", "Registration successful!");
        return "redirect:/login";
    }

}