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
import com.mediciationbox.capstone.medication_app.service.EmailService;
import com.mediciationbox.capstone.medication_app.service.NodeMCUService;
import com.mediciationbox.capstone.medication_app.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationRepository notificationRepository;
    private final IntakeTableRepository intakeTableRepository;
    private final ScheduleService scheduleService;
    private final ActiveUserRepository activeUserRepository;
    private final ScheduleRepository scheduleRepository;
    private final NodeMCUService nodeMCUService;
    private final EmailService emailService;

    public NotificationController(NotificationRepository notificationRepository, IntakeTableRepository intakeTableRepository,
                                  ScheduleService scheduleService, ActiveUserRepository activeUserRepository,
                                  ScheduleRepository scheduleRepository, NodeMCUService nodeMCUService, EmailService emailService){
        this.notificationRepository = notificationRepository;
        this.intakeTableRepository = intakeTableRepository;
        this.scheduleService = scheduleService;
        this.activeUserRepository = activeUserRepository;
        this.scheduleRepository = scheduleRepository;
        this.nodeMCUService = nodeMCUService;
        this.emailService = emailService;
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
        List<Schedule> completedSchedules = new ArrayList<>();
        LocalDateTime recordedTime = latestIntake.getTimeRecorded();
        
        schedulesForToday.forEach(schedule -> {
            LocalDateTime scheduleTime = schedule.getTimeOfIntake();

            // Only process schedules that are NOT already completed
            if (!schedule.isDone() && 
                recordedTime.isAfter(scheduleTime) && 
                recordedTime.isBefore(scheduleTime.plusMinutes(16))) {
                schedule.setDone(true);
                scheduleRepository.save(schedule);
                completedSchedules.add(schedule);
            }
        });
        
        // Send single email notification for all completed medications
        if (!completedSchedules.isEmpty()) {
            try {
                // Format time in 12-hour format (e.g., "11:30 AM", "2:15 PM")
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
                String formattedTime = recordedTime.format(timeFormatter);
                
                // Create message for all completed medications
                StringBuilder medicationList = new StringBuilder();
                for (int i = 0; i < completedSchedules.size(); i++) {
                    if (i > 0) {
                        medicationList.append(", ");
                    }
                    medicationList.append(completedSchedules.get(i).getName());
                }
                
                String message = medicationList.toString() + 
                    (completedSchedules.size() == 1 ? " - Medication Taken at " : " - Medications Taken at ") + 
                    formattedTime;
                
                emailService.sendEmailNotification(activeUser.getEmail(), message);
                
                // Create notification records for each completed medication
                for (Schedule schedule : completedSchedules) {
                    Notification takenNotification = new Notification(
                        schedule.getName() + " - Taken", 
                        schedule.getTimeOfIntake(), 
                        schedule, 
                        LocalDateTime.now()
                    );
                    takenNotification.setUserId(activeUser.getUserId());
                    notificationRepository.save(takenNotification);
                }
                
                log.info("Email notification sent for {} medication(s) taken: {} at {}", 
                    completedSchedules.size(), medicationList.toString(), formattedTime);
                    
            } catch (Exception e) {
                log.error("Failed to send email notification for medication taken: {}", e.getMessage());
            }
        }

        return ResponseEntity.ok().build();
    }

    //Manual Triggering of the ESP buzzer - Simple button press
    @GetMapping("api/trigger-box/buzzer")
    public ResponseEntity<?> triggerBuzzerManually(){
        try {
            // Simply trigger the buzzer without any schedule dependency
            nodeMCUService.triggerBuzzer("Manual Trigger");
            
            // Format time in 12-hour format for user-friendly display
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedTime = LocalDateTime.now().format(timeFormatter);
            
            return ResponseEntity.ok(new ResponseDTO(true, 
                "Buzzer triggered manually at " + formattedTime, 
                null));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(false, "Failed to trigger buzzer: " + e.getMessage(), null));
        }

    }
}
