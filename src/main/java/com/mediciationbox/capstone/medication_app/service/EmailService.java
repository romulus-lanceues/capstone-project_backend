package com.mediciationbox.capstone.medication_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    public EmailService(JavaMailSender javaMailSender){
        this.mailSender = javaMailSender;
    }

    public void sendTestEmail() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo("ramirezjerraldcliff@gmail.com");
        helper.setSubject("Test Email - Medication Reminder");
        helper.setFrom("teammedicationapp@gmail.com");
        
        String htmlContent = createStyledEmailTemplate("Test Medication", "Hoy CJ uminom ka na ng gamot");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    public void sendEmailNotification(String userEmail, String medicationName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(userEmail);
        helper.setSubject("💊 " + medicationName + " - Medication Reminder");
        helper.setFrom("teammedicationapp@gmail.com");
        
        String htmlContent = createStyledEmailTemplate(medicationName, "It's time to take your scheduled medication!");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    private String createStyledEmailTemplate(String medicationName, String message) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html lang=\"en\">")
            .append("<head>")
            .append("<meta charset=\"UTF-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            .append("<title>Medication Reminder</title>")
            .append("<style>")
            .append("body {")
            .append("font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;")
            .append("line-height: 1.6;")
            .append("color: #333;")
            .append("max-width: 600px;")
            .append("margin: 0 auto;")
            .append("padding: 20px;")
            .append("background-color: #f8f9fa;")
            .append("}")
            .append(".container {")
            .append("background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);")
            .append("border-radius: 15px;")
            .append("padding: 30px;")
            .append("box-shadow: 0 10px 30px rgba(0,0,0,0.1);")
            .append("margin: 20px 0;")
            .append("}")
            .append(".header {")
            .append("text-align: center;")
            .append("color: white;")
            .append("margin-bottom: 30px;")
            .append("}")
            .append(".medication-icon {")
            .append("font-size: 48px;")
            .append("margin-bottom: 15px;")
            .append("display: block;")
            .append("}")
            .append(".medication-name {")
            .append("font-size: 28px;")
            .append("font-weight: bold;")
            .append("margin-bottom: 10px;")
            .append("text-shadow: 2px 2px 4px rgba(0,0,0,0.3);")
            .append("}")
            .append(".reminder-text {")
            .append("font-size: 18px;")
            .append("opacity: 0.9;")
            .append("}")
            .append(".content {")
            .append("background: white;")
            .append("border-radius: 10px;")
            .append("padding: 25px;")
            .append("margin: 20px 0;")
            .append("box-shadow: 0 5px 15px rgba(0,0,0,0.1);")
            .append("}")
            .append(".message {")
            .append("font-size: 16px;")
            .append("color: #555;")
            .append("text-align: center;")
            .append("margin-bottom: 20px;")
            .append("}")
            .append(".time-info {")
            .append("background: #e3f2fd;")
            .append("border-left: 4px solid #2196f3;")
            .append("padding: 15px;")
            .append("margin: 20px 0;")
            .append("border-radius: 5px;")
            .append("}")
            .append(".footer {")
            .append("text-align: center;")
            .append("color: #666;")
            .append("font-size: 14px;")
            .append("margin-top: 30px;")
            .append("padding-top: 20px;")
            .append("border-top: 1px solid #eee;")
            .append("}")
            .append(".app-name {")
            .append("color: #667eea;")
            .append("font-weight: bold;")
            .append("}")
            .append(".highlight {")
            .append("background: linear-gradient(120deg, #a8edea 0%, #fed6e3 100%);")
            .append("padding: 2px 6px;")
            .append("border-radius: 4px;")
            .append("font-weight: bold;")
            .append("}")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class=\"container\">")
            .append("<div class=\"header\">")
            .append("<span class=\"medication-icon\">💊</span>")
            .append("<div class=\"medication-name\">").append(medicationName).append("</div>")
            .append("<div class=\"reminder-text\">Medication Reminder</div>")
            .append("</div>")
            .append("</div>")
            .append("<div class=\"content\">")
            .append("<div class=\"message\">")
            .append("<span class=\"highlight\">").append(message).append("</span>")
            .append("</div>")
            .append("<div class=\"time-info\">")
            .append("<strong>⏰ Time:</strong> Please take your medication as scheduled<br>")
            .append("<strong>📅 Date:</strong> ").append(java.time.LocalDate.now()).append("<br>")
            .append("<strong>🕐 Current Time:</strong> ").append(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
            .append("</div>")
            .append("</div>")
            .append("<div class=\"footer\">")
            .append("<p>This is an automated reminder from <span class=\"app-name\">MedicationBox</span></p>")
            .append("<p>Please don't reply to this email. For support, contact our team.</p>")
            .append("</div>")
            .append("</body>")
            .append("</html>");
        
        return html.toString();
    }

}
