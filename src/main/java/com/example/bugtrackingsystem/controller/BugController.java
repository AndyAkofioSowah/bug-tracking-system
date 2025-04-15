package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.repository.BugRepository;
import com.example.bugtrackingsystem.utils.BugClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.commons.text.similarity.CosineSimilarity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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


    @GetMapping("/check-duplicate")
    @ResponseBody
    public String checkDuplicateBug(@RequestParam("title") String title,
                                    @RequestParam("description") String description,
                                    Model model) {
        Logger logger = LoggerFactory.getLogger(BugController.class);
        BugClassifier classifier = new BugClassifier();

        List<Bug> existingBugs = bugRepository.findAll();
        Map<CharSequence, Integer> newBugVector = classifier.textToVector(title + " " + description);

        logger.info("Checking for duplicates: [{}] - [{}]", title, description);

        for (Bug bug : existingBugs) {
            Map<CharSequence, Integer> existingBugVector = classifier.textToVector(bug.getTitle() + " " + bug.getDescription());

            // Compute similarity
            double similarityScore = classifier.computeSimilarity(newBugVector, existingBugVector);

            logger.info("Comparing with existing bug: [{}] - [{}]", bug.getTitle(), bug.getDescription());
            logger.info("Similarity Score: {}", similarityScore);

            if (similarityScore > 0.75) {
                logger.info("🚨 Duplicate detected! Redirecting to warning page.");
                model.addAttribute("duplicateBug", bug);
                return "duplicate-warning"; // Force warning page
            }
        }

        logger.info("No duplicate found.");
        return "No Duplicate Found";
    }



    private Map<CharSequence, Integer> textToVector(String text) {
        Map<CharSequence, Integer> wordCount = new HashMap<>();

        // Convert to lowercase and remove punctuation
        text = text.toLowerCase().replaceAll("[^a-zA-Z ]", "");

        for (String word : text.split("\\s+")) { // Split by spaces
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        return wordCount;
    }


    @PostMapping("/submit-anyway")
    public String submitAnyway(@RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("priority") String priority,
                               @RequestParam("tempFilePath") String tempFilePath,
                               RedirectAttributes redirectAttributes) {

        try {
            String category = BugClassifier.classifyBug(title, description);
            Bug bug = new Bug(title, description, priority, "Open", category);

            // Handle the file saved earlier
            if (tempFilePath != null && !tempFilePath.isEmpty()) {
                Path sourcePath = Paths.get("uploads/temp/" + tempFilePath);
                Path destinationPath = Paths.get("uploads/" + tempFilePath);

                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                bug.setFilePath(tempFilePath);
            }

            bugRepository.save(bug);
            redirectAttributes.addFlashAttribute("successMessage", "Bug reported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to report the bug.");
        }

        return "redirect:/dashboard";
    }



    @PostMapping("/report-bug")
    public String reportBug(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("priority") String priority,
                            @RequestParam("file") MultipartFile file,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        Logger logger = LoggerFactory.getLogger(BugController.class);
        logger.info("Received bug report: [{}] - [{}]", title, description);

        try {
            // Check for duplicates before saving
            List<Bug> existingBugs = bugRepository.findAll();
            Map<CharSequence, Integer> newBugVector = textToVector(title + " " + description);

            for (Bug bug : existingBugs) {
                Map<CharSequence, Integer> existingBugVector = textToVector(bug.getTitle() + " " + bug.getDescription());
                double similarity = new CosineSimilarity().cosineSimilarity(newBugVector, existingBugVector);
                logger.info("Comparing with existing bug [{}]: Similarity Score = {}", bug.getTitle(), similarity);

                if (similarity > 0.75) {  // Threshold for duplicate detection
                    model.addAttribute("duplicateBug", bug);
                    model.addAttribute("submittedTitle", title);
                    model.addAttribute("submittedDescription", description);
                    model.addAttribute("submittedPriority", priority);



                    // Save temp file if exists
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        assert originalFilename != null;
                        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                        String filename = System.currentTimeMillis() + "_temp_" + originalFilename;

                        List<String> allowedFormats = List.of(".jpg", ".jpeg", ".png", ".mp4", ".mov", ".avi");
                        if (!allowedFormats.contains(extension)) {
                            redirectAttributes.addFlashAttribute("errorMessage", "Invalid file format.");
                            return "redirect:/dashboard";
                        }

                        if (file.getSize() > 10 * 1024 * 1024) {
                            redirectAttributes.addFlashAttribute("errorMessage", "File size exceeds the 10MB limit.");
                            return "redirect:/dashboard";
                        }

                        Path tempFilePath = Paths.get("uploads/temp/" + filename);
                        Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
                        model.addAttribute("tempFilePath", filename);
                    }

                    return "duplicate-warning"; // Show warning page with context
                }
            }

            logger.info("✅ No duplicate found. Proceeding with submission.");

            // If no duplicate, save the bug
            String category = BugClassifier.classifyBug(title, description);
            Bug bug = new Bug(title, description, priority, "Open", category);

            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                assert originalFilename != null;
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                String filename = System.currentTimeMillis() + "_" + originalFilename;

                List<String> allowedFormats = List.of(".jpg", ".jpeg", ".png", ".mp4", ".mov", ".avi");
                if (!allowedFormats.contains(extension)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid file format.");
                    return "redirect:/dashboard";
                }

                if (file.getSize() > 10 * 1024 * 1024) {
                    redirectAttributes.addFlashAttribute("errorMessage", "File size exceeds the 10MB limit.");
                    return "redirect:/dashboard";
                }

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

