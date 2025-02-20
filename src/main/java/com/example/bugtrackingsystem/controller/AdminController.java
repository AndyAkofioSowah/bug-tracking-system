package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.repository.BugRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
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


    @PostMapping("/update") // Fix the mapping, no need for "/admin/update"
    public String updateBugStatus(@RequestParam Long bugId, @RequestParam String status, RedirectAttributes redirectAttributes) {
        Bug bug = bugRepository.findById(bugId).orElse(null);
        if (bug != null) {
            bug.setStatus(status);
            bugRepository.save(bug);
            redirectAttributes.addFlashAttribute("successMessage", "Bug status updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update bug status.");
        }
        return "redirect:/admin"; // Redirect back to admin page
    }


}

