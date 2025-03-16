package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.repository.BugRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BugRepository bugRepository;

    @GetMapping("/admin")
    public String adminPage(HttpServletRequest request, Model model) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);
        return "admin";
    }



    @GetMapping
    public String adminDashboard(
            @RequestParam(name = "orderBy", required = false, defaultValue = "recent") String orderBy,
            @RequestParam(name = "status", required = false, defaultValue = "all") String statusFilter,
            Model model) {

        List<Bug> bugs;

        if ("all".equals(statusFilter)) {
            bugs = bugRepository.findAll(); // Fetch all bugs
        } else {
            bugs = bugRepository.findByStatusIgnoreCase(statusFilter); // Fetch bugs based on status
        }

        // Sorting logic
        if ("urgency".equals(orderBy)) {
            bugs = bugs.stream()
                    .sorted((b1, b2) -> priorityValue(b2.getPriority()) - priorityValue(b1.getPriority()))
                    .collect(Collectors.toList());
        } else {
            bugs = bugs.stream()
                    .sorted((b1, b2) -> b2.getId().compareTo(b1.getId()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("bugs", bugs);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("statusFilter", statusFilter);

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

