package com.example.bugtrackingsystem.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageController
{

    @GetMapping("/view-image/{filePath}")
    public String viewImage(@PathVariable String filePath, Model model)
    {
        model.addAttribute("filePath", "/uploads/" + filePath);
        return "view-image"; // Renders view-image.html
    }


    @GetMapping("/view-video/{filePath}")
    public ResponseEntity<Resource> viewVideo(@PathVariable String filePath) {
        try {
            Path file = Paths.get("uploads/").resolve(filePath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("video/mp4")) // Supports mp4 videos
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
