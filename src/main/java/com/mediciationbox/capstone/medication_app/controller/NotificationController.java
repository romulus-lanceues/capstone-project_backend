package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.dto.ResponseDTO;
import com.mediciationbox.capstone.medication_app.exception.EmptyIntakeTableException;
import com.mediciationbox.capstone.medication_app.exception.NoActiveUserException;
import com.mediciationbox.capstone.medication_app.model.ActiveUser;
import com.mediciationbox.capstone.medication_app.model.IntakeTable;
import com.mediciationbox.capstone.medication_app.model.Notification;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.repository.ActiveUserRepository;
import com.mediciationbox.capstone.medication_app.repository.IntakeTableRepository;
import com.mediciationbox.capstone.medication_app.repository.NotificationRepository;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import com.mediciationbox.capstone.medication_app.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final IntakeTableRepository intakeTableRepository;
    private final ScheduleService scheduleService;
    private final ActiveUserRepository activeUserRepository;
    private final ScheduleRepository scheduleRepository;

    public NotificationController(NotificationRepository notificationRepository, IntakeTableRepository intakeTableRepository,
                                  ScheduleService scheduleService, ActiveUserRepository activeUserRepository,
                                  ScheduleRepository scheduleRepository){
        this.notificationRepository = notificationRepository;
        this.intakeTableRepository = intakeTableRepository;
        this.scheduleService = scheduleService;
        this.activeUserRepository = activeUserRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/api/get-notifications/{id}")
    public ResponseEntity<?> getNotifications(@PathVariable Long id){
        return ResponseEntity.ok(notificationRepository.getAllNotificationsForUser(id));
    }

    //Catch ESP 32's notification

    @GetMapping("/api/notify-backend/medication-taken")
    public ResponseEntity<String> medicationTaken(){
        System.out.println("Notification received");

        // Retrieve the latest sensor record
        IntakeTable latestIntake = intakeTableRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new EmptyIntakeTableException("Intake table is empty"));

        // Identify the active user
        ActiveUser activeUser = activeUserRepository.findByActiveStatus();

        if (activeUser == null) {
            throw new NoActiveUserException("No active user as of now.");
        }

        // Retrieve schedules for today
        List<Schedule> schedulesForToday = scheduleService.retrieveScheduleForToday(activeUser.getUserId());

        // Check for matching schedules and mark as done
        schedulesForToday.forEach(schedule -> {
            LocalDateTime scheduleTime = schedule.getTimeOfIntake();
            LocalDateTime recordedTime = latestIntake.getTimeRecorded();

            // Was taken within 15 minutes after reminder
            if (recordedTime.isAfter(scheduleTime) && recordedTime.isBefore(scheduleTime.plusMinutes(16))) {
                schedule.setDone(true);
                scheduleRepository.save(schedule);
            }
        });

        return ResponseEntity.ok().build();
    }

    //Manual Triggering of the ESP buzzer

}
