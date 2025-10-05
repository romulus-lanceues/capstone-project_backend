package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.dto.ResponseDTO;
import com.mediciationbox.capstone.medication_app.model.Notification;
import com.mediciationbox.capstone.medication_app.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {
    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/api/get-notifications/{id}")
    public ResponseEntity<?> getNotifications(@PathVariable Long id){
        return ResponseEntity.ok(notificationRepository.getAllNotificationsForUser(id));
    }
}
