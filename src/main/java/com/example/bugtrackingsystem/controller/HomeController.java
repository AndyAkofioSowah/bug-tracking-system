package com.example.bugtrackingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showWelcomePage() {
        return "welcome";
    }

}
