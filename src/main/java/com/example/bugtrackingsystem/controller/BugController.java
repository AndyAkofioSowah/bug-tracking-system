package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class BugController {

    @Autowired
    private BugRepository bugRepository;

    @GetMapping("/bug/{id}")
    public String bugDetails(@PathVariable Long id, Model model) {
        Bug bug = bugRepository.findById(id).orElse(null);
        if (bug == null) {
            return "error-page"; // Redirect if bug not found
        }

        model.addAttribute("bug", bug);
        return "bug-details";
    }

    @PostMapping("/report-bug")
    public String reportBug(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("priority") String priority,
                            @RequestParam("file") MultipartFile file,
                            RedirectAttributes redirectAttributes) {
        try {
            Bug bug = new Bug(title, description, priority);

            if (!file.isEmpty()) {
                // Generate a unique filename
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                String filename = System.currentTimeMillis() + "_" + originalFilename;

                // Define accepted file formats
                List<String> allowedFormats = List.of(".jpg", ".jpeg", ".png", ".mp4", ".mov", ".avi");
                if (!allowedFormats.contains(extension)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid file format.");
                    return "redirect:/dashboard";
                }

                // Define max file size (10MB)
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                    redirectAttributes.addFlashAttribute("errorMessage", "File size exceeds the 10MB limit.");
                    return "redirect:/dashboard";
                }

                // Save the file
                Path filePath = Paths.get("uploads/" + filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                bug.setFilePath(filename); // Store filename in the database
            }

            bugRepository.save(bug);
            redirectAttributes.addFlashAttribute("successMessage", "Bug reported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to report the bug.");
        }

        return "redirect:/dashboard";
    }

}

