package com.example.bugtrackingsystem.controller;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.entity.Company;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.BugRepository;
import com.example.bugtrackingsystem.repository.UserRepository;
import com.example.bugtrackingsystem.service.CompanyService;
import com.example.bugtrackingsystem.utils.BugClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BugRepository bugRepository;

    @Value("${upload.dir}")
    private String uploadDir;


    private long countBugsByStatus(List<Bug> bugs, String status) {
        return bugs.stream().filter(b -> b.getStatus().equalsIgnoreCase(status)).count();
    }






    @GetMapping("/search")
    public String searchBugs(@RequestParam String query, Model model) {

        List<Bug> results = bugRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCompany_CompanyNameContainingIgnoreCase(query, query, query);

        model.addAttribute("bugs", results);
        return "dashboard";
    }

    @GetMapping
    public String showDashboard(Model model) {
        List<Company> companies = companyService.getAllCompanies();
        model.addAttribute("companies", companies);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User userDetails) {
            model.addAttribute("username", userDetails.getUsername());
        }



        List<Bug> bugs = bugRepository.findAll();
        model.addAttribute("bugs", bugs);

        return "dashboard";
    }



    // Show Bug Details Page for Users
    @GetMapping("/details")
    public String showBugDetails(@RequestParam Long bugId, Model model) {
        Bug bug = bugRepository.findById(bugId).orElse(null);
        if (bug == null) {
            return "error"; // Handle error if bug doesn't exist
        }
        model.addAttribute("bug", bug);
        return "bug-details";
    }



    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmailWithBugs(email);
    }

/*
    // Handle bug report submissions
    @PostMapping("/submit")
    public String submitBugReport(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String priority,
            @RequestParam Long companyId, // Gets selected company
            @RequestParam(required = false) String category, //
            @RequestParam(required = false) MultipartFile file,
            Model model) {

        // Check for duplicate bug
        if (bugRepository.existsByTitleIgnoreCaseAndDescriptionIgnoreCase(title, description)) {
            model.addAttribute("message", "Duplicate bug detected! Please check existing reports. If you think a mistake has been made, please contact customer service.");
            return "error";
        }

        // Auto-classify if category is not supplied
        if (category == null || category.isBlank()) {
            category = BugClassifier.classifyBug(title, description);
        }

        // Create new bug object
        Bug newBug = new Bug(title, description, priority);
        newBug.setCategory(category);

        // Associate the bug with the selected company
        Company selectedCompany = companyService.getCompanyById(companyId);
        newBug.setCompany(selectedCompany); // This is crucial


        // Handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                String savedFilePath = saveFile(file);
                newBug.setFilePath(savedFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("message", "File upload failed due to an internal error.");
                return "error";
            }
        }

        System.out.println("Bug submitted for company: " + selectedCompany.getCompanyName());
        newBug.setSubmittedBy(getCurrentUser());


        // Save bug
        bugRepository.save(newBug);

        model.addAttribute("message", "Bug reported successfully!");
        return "success";
    }

*/
    @PostMapping("/update")
    public String updateBugStatus(@RequestParam Long bugId, @RequestParam String status) {
        Bug bug = bugRepository.findById(bugId).orElse(null);
        if (bug != null) {
            bug.setStatus(status);
            bugRepository.save(bug);
        }
        return "redirect:/dashboard";
    }


    // Helper method to save the uploaded file
    private String saveFile(MultipartFile file) throws IOException {
        Path dirPath = Paths.get("uploads");
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = dirPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());
        return uniqueFileName; // Only return the relative filename
    }


}

