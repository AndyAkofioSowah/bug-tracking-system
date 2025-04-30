package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.entity.Company;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.BugRepository;
import com.example.bugtrackingsystem.repository.CompanyRepository;
import com.example.bugtrackingsystem.repository.UserRepository;
import com.example.bugtrackingsystem.service.CompanyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.bugtrackingsystem.entity.User;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/dashboard/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BugRepository bugRepository;

    @GetMapping("/admin")
    public String adminPage(HttpServletRequest request, Model model) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);
        return "admin";
    }

    @GetMapping("/profile/admin")
    public String showAdminProfile(Model model, Authentication authentication,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "3") int size) {
        System.out.println("HIT /profile/admin");
        String email = authentication.getName();
        User admin = userRepository.findByEmailWithBugs(email);

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/error";
        }

        Company company = companyRepository.findByAdmin(admin);

        // Fix here: Find only bugs for this company, paginated
        Pageable pageable = PageRequest.of(page, size);
        Page<Bug> bugPage = bugRepository.findByCompany(company, pageable); // ➔ you must create this repo method

        long totalBugs = bugPage.getTotalElements();
        long openBugs = bugPage.getContent().stream().filter(bug -> "Open".equalsIgnoreCase(bug.getStatus())).count();
        long inprogressBugs = bugPage.getContent().stream().filter(bug -> "in-progress".equalsIgnoreCase(bug.getStatus())).count();
        long closedBugs = bugPage.getContent().stream().filter(bug -> "Resolved".equalsIgnoreCase(bug.getStatus())).count();

        model.addAttribute("bugs", bugPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bugPage.getTotalPages());

        model.addAttribute("admin", admin);
        model.addAttribute("company", company);
        model.addAttribute("totalBugs", totalBugs);
        model.addAttribute("openBugs", openBugs);
        model.addAttribute("inprogressBugs", inprogressBugs);
        model.addAttribute("closedBugs", closedBugs);

        return "admin_profile";
    }




    @GetMapping
    public String AdminDashboard(@RequestParam(value = "orderBy", required = false) String orderBy, Model model) {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();

        // Get the admin user

        User admin = userRepository.findByEmailWithBugs(adminEmail);
        if (admin == null) {
            model.addAttribute("error", "Admin not found.");
            return "error";
        }

        // Get the company for this admin
        Company company = companyService.getCompanyByAdmin(admin);

        if (company == null) {
            model.addAttribute("error", "No company assigned to this admin.");
            return "error";
        }

        model.addAttribute("companyName", company.getCompanyName());


        System.out.println("Admin company: " + company.getCompanyName());

        // Fetch only bugs for this company
        List<Bug> bugs = bugRepository.findByCompany(company);

// Sort bugs based on the selected order
        if ("urgency".equalsIgnoreCase(orderBy)) {
            bugs.sort(Comparator.comparingInt(b -> switch (b.getPriority().toLowerCase()) {
                case "high" -> -3;
                case "medium" -> -2;
                case "low" -> -1;
                default -> 0;
            }));
        } else if ("recent".equalsIgnoreCase(orderBy)) {
            bugs.sort(Comparator.comparing(Bug::getReportedAt).reversed());
        }


        model.addAttribute("orderBy", orderBy);
        model.addAttribute("totalBugs", bugs.size());
        model.addAttribute("bugsResolved", bugs.stream().filter(b -> "Resolved".equalsIgnoreCase(b.getStatus())).count());
        model.addAttribute("bugsOpen", bugs.stream().filter(b -> "Open".equalsIgnoreCase(b.getStatus())).count());
        model.addAttribute("bugs", bugs);

        return "admin";
    }


    // Convert Priority Levels to Numerical Values for Sorting
    private int priorityValue(String priority) {
        return switch (priority.toLowerCase()) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };
    }
    // Admin can update bug status


    @CrossOrigin
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateBugStatus(@RequestParam Long bugId, @RequestParam String status) {
        if (bugId == null || status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body("Invalid request parameters.");
        }

        Optional<Bug> optionalBug = bugRepository.findById(bugId);
        if (optionalBug.isPresent()) {
            Bug bug = optionalBug.get();
            bug.setStatus(status);
            bugRepository.save(bug);
            return ResponseEntity.ok("Bug status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bug not found.");
        }
    }






}

