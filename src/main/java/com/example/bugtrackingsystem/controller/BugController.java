package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}

