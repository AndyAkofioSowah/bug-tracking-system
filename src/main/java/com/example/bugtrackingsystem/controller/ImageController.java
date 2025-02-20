package com.example.bugtrackingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ImageController {

    @GetMapping("/view-image/{filePath}")
    public String viewImage(@PathVariable String filePath, Model model) {
        model.addAttribute("filePath", "/uploads/" + filePath);
        return "view-image"; // Renders view-image.html
    }
}
