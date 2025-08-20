package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.model.Notification;
import com.mediciationbox.capstone.medication_app.repository.NotificationRepository;
import com.mediciationbox.capstone.medication_app.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NotificationController {

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;

    public NotificationController(NotificationRepository notificationRepository, NotificationService notificationService){
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("api/notifications")
    public List<Notification> getAllNotifications(){
        return  notificationRepository.findAll();
    }

    @PostMapping("/api/notification/generate/{scheduleId}")
    public ResponseEntity<?> createNotification(@PathVariable Integer scheduleId){
        notificationService.createNotification(scheduleId);
       return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
