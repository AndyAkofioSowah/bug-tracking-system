package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.ContactMessage;
import com.example.bugtrackingsystem.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    @Autowired
    private ContactMessageRepository messageRepository;

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContactForm(@ModelAttribute ContactMessage contactMessage, Model model) {
        messageRepository.save(contactMessage);
        model.addAttribute("successMessage", "Your message has been successfully submitted to the Customer Service!");
        return "contact";
    }

    @GetMapping("/overseer/messages")
    public String viewMessages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "overseer-messages";
    }
}
