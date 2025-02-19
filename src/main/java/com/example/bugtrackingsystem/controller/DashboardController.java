package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
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
    private BugRepository bugRepository;

    @Value("${upload.dir}")
    private String uploadDir;


    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {
        model.addAttribute("totalBugs", bugRepository.count());
        model.addAttribute("bugsResolved", bugRepository.countByStatus("Resolved"));
        model.addAttribute("bugsOpen", bugRepository.countByStatus("Open"));
        model.addAttribute("bugs", bugRepository.findAll());
        return "admin";
    }



    @GetMapping("/search")
    public String searchBugs(@RequestParam String query, Model model) {
        List<Bug> results = bugRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        model.addAttribute("bugs", results);
        return "dashboard";
    }

    @GetMapping
    public String showDashboard(Model model) {

        //  Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure authentication is not null
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            //Add username to the model
            model.addAttribute("username", userDetails.getUsername());
        }

        //  Fetch all bugs (Admins can see reports)
        List<Bug> bugs = bugRepository.findAll();
        model.addAttribute("bugs", bugs);
        return "dashboard";
    }




    // Handle bug report submissions
    @PostMapping("/submit")
    public String submitBugReport(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String priority,
            @RequestParam(required = false) MultipartFile file,
            Model model) {

        // Check for duplicate bug
        if (bugRepository.existsByTitleIgnoreCaseAndDescriptionIgnoreCase(title, description)) {
            model.addAttribute("message", "Duplicate bug detected! Please check existing reports.");
            return "error"; // Render error.html for user feedback
        }

        // Create new bug object
        Bug newBug = new Bug(title, description, priority);

        // Validate and save the file if it exists
        // Save the uploaded file if it exists
        if (file != null && !file.isEmpty()) {
            try {
                String savedFilePath = saveFile(file);
                newBug.setFilePath(savedFilePath); // Save the file path in the Bug entity
            } catch (IOException e) {
                e.printStackTrace(); // Log the exception
                model.addAttribute("message", "File upload failed due to an internal error.");
                return "error";
            }
        }

        // Save bug to the database
        bugRepository.save(newBug);

        // Provide success feedback
        model.addAttribute("message", "Bug reported successfully!");
        return "success"; // Render success.html
    }



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

