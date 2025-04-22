package com.example.bugtrackingsystem.controller;

import com.example.bugtrackingsystem.service.CompanyVerificationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompanyVerificationController {

    @Autowired
    private CompanyVerificationService verificationService;

    @GetMapping("/verify-company")
    public Map<String, String> verifyCompany(@RequestParam String number) {
        Map<String, String> data = verificationService.fetchCompanyData(number);
        if (data != null) {
            return Map.of(
                    "status", "FOUND",
                    "name", data.get("name"),
                    "dateOfCreation", data.get("date")
            );
        } else {
            return Map.of("status", "NOT_FOUND");
        }
    }

}
