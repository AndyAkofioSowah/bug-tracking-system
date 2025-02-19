package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BugRepository bugRepository;

    @GetMapping
    public String adminDashboard(Model model) {
        List<Bug> bugs = bugRepository.findAll(); // Fetch all bugs for admin

        // Log file paths for debugging
        bugs.forEach(bug -> System.out.println("Bug filePath: " + bug.getFilePath()));

        model.addAttribute("bugs", bugs);
        return "admin"; // Render the `admin.html` view
    }
}
