package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.Bug;
import com.example.bugtrackingsystem.entity.User;
import com.example.bugtrackingsystem.repository.BugRepository;
import com.example.bugtrackingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BugRepository bugRepository;
    @GetMapping
    public String showProfile(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmailWithBugs(email);
        if (user == null) {
            return "error"; //
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Bug> bugPage = bugRepository.findBySubmittedBy(user, pageable);  // paginated bugs for this user

        model.addAttribute("user", user);
        model.addAttribute("submittedBugs", bugPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bugPage.getTotalPages());

        return "profile";
    }


}
