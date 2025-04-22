package com.example.bugtrackingsystem.controller;
import com.example.bugtrackingsystem.entity.Company;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.CompanyRepository;
import com.example.bugtrackingsystem.repository.UserRepository;
import com.example.bugtrackingsystem.service.CompaniesHouseService;
import com.example.bugtrackingsystem.service.CompanyVerificationService;
import com.example.bugtrackingsystem.utils.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
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
    private CompanyVerificationService verificationService;

    @Autowired
    private CompaniesHouseService companiesHouseService;

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
            @RequestParam(required = false) String companyNumber,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate password
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

        // Prevent duplicate accounts
        if (userRepository.findByEmailWithBugs(loginEmail) != null) {
            model.addAttribute("errorMessage", "An account with this email already exists.");
            return "register";
        }
        if (userRepository.findByUsername(displayUsername) != null) {
            model.addAttribute("errorMessage", "This username is already taken.");
            return "register";
        }

        // If admin, verify company **BEFORE creating user**
        if (role.equals("ADMIN")) {
            String officialCompanyName = verificationService.getOfficialCompanyName(companyNumber);
            if (officialCompanyName == null) {
                model.addAttribute("errorMessage", "Could not verify your company with Companies House.");
                return "register";
            }
            // Optional warning on name mismatch
            if (!officialCompanyName.equalsIgnoreCase(companyName)) {
                model.addAttribute("warningMessage", "Company name does not exactly match Companies House. Proceeding anyway.");
            }
            companyName = officialCompanyName; // use verified name
        }

        // Create user
        User newUser = new User();
        newUser.setUsername(displayUsername);
        newUser.setEmail(loginEmail);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);

        if (dob != null && !dob.isEmpty()) {
            try {
                Date parsedDob = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
                newUser.setDateOfBirth(parsedDob);
            } catch (ParseException e) {
                model.addAttribute("errorMessage", "Invalid date format.");
                return "register";
            }
        }

        userRepository.save(newUser);

        // Save company if ADMIN
        // Save company if ADMIN
        if (role.equals("ADMIN")) {
            Company company = new Company();
            company.setAdmin(newUser);
            company.setCompanyName(companyName);
            company.setCompanyEmail(companyEmail);
            company.setCompanyNumber(companyNumber);

            String dateStr = request.getParameter("dateEstablished");
            Date dateEstablished = null;
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    dateEstablished = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                } catch (ParseException e) {
                    model.addAttribute("errorMessage", "Invalid date format.");
                    return "register";
                }
            }

            try {
                dateEstablished = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

                // Check that the year is reasonable (e.g. not more than 100 years in future)
                assert dateStr != null;
                int year = Integer.parseInt(dateStr.substring(0, 4));
                if (year > 2100 || year < 1800) {
                    model.addAttribute("errorMessage", "Please enter a valid year for the date established.");
                    return "register";
                }

            } catch (ParseException | NumberFormatException e) {
                model.addAttribute("errorMessage", "Invalid date format.");
                return "register";
            }


            company.setDateEstablished(dateEstablished); // even if null, better to be explicit
            companyRepository.save(company); // // move this out
        }

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful!");
        return "redirect:/login";
    }


}