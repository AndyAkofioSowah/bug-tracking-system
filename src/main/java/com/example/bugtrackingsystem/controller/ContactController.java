package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.entity.ContactMessage;
import com.example.bugtrackingsystem.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class ContactController {

    @Autowired
    private ContactMessageRepository messageRepository;

    @GetMapping("/contact")
    public String showContactForm(Model model, Authentication authentication) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact"; // no need to inject roles now since Thymeleaf handles it
    }

    @PostMapping("/contact")
    public String submitContactForm(@ModelAttribute ContactMessage contactMessage, Model model) {
        messageRepository.save(contactMessage);
        model.addAttribute("successMessage", "Your message has been successfully submitted to the Customer Service!");
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact";
    }

    @GetMapping("/overseer/messages")
    public String viewMessages(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "overseer-messages";
    }

    @GetMapping("/overseer/messages/{id}")
    public String viewMessageDetails(@PathVariable Long id, Model model) {
        ContactMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message ID: " + id));
        model.addAttribute("message", message);
        return "message-details";
    }
}