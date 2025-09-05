package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailNotificationController {

    private EmailService emailService;

    public EmailNotificationController(EmailService emailService){
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public String testEmail(){
        try{
            emailService.sendTestEmail();
            return "Email Sent Successfully";
        }
        catch (Exception e){
           return e.getMessage();
        }
    }
}
