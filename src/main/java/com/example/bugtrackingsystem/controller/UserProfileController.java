package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.BugRepository;
import com.example.bugtrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BugRepository bugRepository;

    @GetMapping
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmailWithBugs(email);
        if (user == null) {
            return "error";
        }

        List<Bug> userBugs = bugRepository.findBySubmittedBy(user);

        System.out.println("User loaded: " + user.getEmail());
        System.out.println("Username: " + user.getUsername());
        System.out.println("DOB: " + user.getDateOfBirth());
        System.out.println("Submitted Bugs: " + user.getSubmittedBugs());


        model.addAttribute("user", user);
        model.addAttribute("submittedBugs", userBugs);

        return "profile";
    }
}
