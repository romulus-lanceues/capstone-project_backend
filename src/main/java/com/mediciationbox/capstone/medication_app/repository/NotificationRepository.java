package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.Notification;
import com.mediciationbox.capstone.medication_app.model.NotificationStatus;
import com.mediciationbox.capstone.medication_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserAndStatus(User user, NotificationStatus status);
    List<Notification> findByStatusAndScheduledTimeBefore(NotificationStatus status, LocalDateTime getScheduledTime);
}
