package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    //Using a JPQL Query
//    @Query("SELECT s from Notification s WHERE s.name = :name AND s.scheduleTime = :scheduleTime")
//    Optional<Notification> findANotification(@Param("scheduleName") String scheduleName, @Param("scheduleTime") LocalDateTime scheduleTime);
}
