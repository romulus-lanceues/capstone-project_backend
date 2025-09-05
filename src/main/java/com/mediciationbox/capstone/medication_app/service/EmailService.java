package com.mediciationbox.capstone.medication_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    public EmailService(JavaMailSender javaMailSender){
        this.mailSender = javaMailSender;
    }

    public void sendTestEmail(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("ramirezjerraldcliff@gmail.com");
        message.setSubject("Test Email");
        message.setText("Hoy CJ uminom ka na ng gamot");
        message.setFrom("teammedicationapp@gmail.com");

        mailSender.send(message);

    }
    public void sendEmailNotification(String userEmail, String medicationName){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject(medicationName + " needs to be taken soon");
        message.setText("A scheduled medication needs to be taken soon!");
        message.setFrom("teammedicationapp@gmail.com");

        mailSender.send(message);
    }

}
