package com.mediciationbox.capstone.medication_app.config;



import com.mediciationbox.capstone.medication_app.dto.SchedulesForTodayEvent;
import com.mediciationbox.capstone.medication_app.exception.NoExistingScheduleException;
import com.mediciationbox.capstone.medication_app.model.ActiveUser;
import com.mediciationbox.capstone.medication_app.model.Notification;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.repository.ActiveUserRepository;
import com.mediciationbox.capstone.medication_app.repository.NotificationRepository;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import com.mediciationbox.capstone.medication_app.service.EmailService;
import com.mediciationbox.capstone.medication_app.service.NodeMCUService;
import com.mediciationbox.capstone.medication_app.service.ScheduleService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ScheduledTasks {
    private final ScheduleService scheduleService;
    private final ActiveUserRepository activeUserRepository;
    private final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager;
    private List<Schedule> schedulesForToday;
    private final ScheduleRepository scheduleRepository;
    private final NodeMCUService nodeMCUService;


    public ScheduledTasks(ScheduleService scheduleService,
                          ActiveUserRepository activeUserRepository, EmailService emailService,
                          NotificationRepository notificationRepository, EntityManager entityManager,
                          ScheduleRepository scheduleRepository, NodeMCUService nodeMCUService){
        this.scheduleService = scheduleService;
        this.activeUserRepository = activeUserRepository;
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
        this.entityManager = entityManager;
        this.scheduleRepository = scheduleRepository;
        this.nodeMCUService = nodeMCUService;
    }

    //Automate Schedule creation for a new day
    @Scheduled(cron = "0 0 0 * * *") // Every day at 12:00 AM
    public void generateDailySchedules() {
        try {
            ActiveUser activeUser = activeUserRepository.findByActiveStatus();
            if (activeUser != null) {
                schedulesForToday =  scheduleService.retrieveScheduleForToday(activeUser.getUserId());

            }
        } catch (Exception e) {
            // Log the error for debugging
            log.error("Failed to generate daily schedules", e);
        }
    }

    @EventListener
    public void updateSchedulesForToday(SchedulesForTodayEvent schedulesForTodayEvent){
        if(schedulesForTodayEvent.getDateForToday().equals(LocalDate.now())){
           this.schedulesForToday = schedulesForTodayEvent.getSchedulesForToday();
        }
    }


    @Transactional
    @Scheduled(cron = "0 * * * * *")  // Runs at exactly 0 seconds of every minute (e.g., 09:31:00, 09:32:00)
    public void sendNotification(){
        if(schedulesForToday == null || schedulesForToday.isEmpty()){
            return;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        schedulesForToday.stream().forEach( schedule -> {

            /*
            Schedules are collected in a different class and there's no active hibernate connection here.
            Reattaching / Re-fetching is the appropriate solution.

            ***This can overwrite updated data******

            Schedule currentSchedule = entityManager.merge(schedule);
            */

            //Get the id
            Optional<Schedule> retrievedSchedule = scheduleRepository.findById(schedule.getId());

            //Check if it's empty for safety
            Schedule currentSchedule = retrievedSchedule.orElseThrow(() -> new NoExistingScheduleException("No existing found to send notification"));


            LocalDateTime scheduleTime = currentSchedule.getTimeOfIntake();

            // Send notification exactly 3 minutes before schedule time (since we run at exact minutes)
            if(currentTime.truncatedTo(ChronoUnit.MINUTES).equals(scheduleTime.minusMinutes(3).truncatedTo(ChronoUnit.MINUTES))){
                //Check if this schedule already produced a notification.
                List<Notification> notifications = currentSchedule.getNotifications();

                if(notifications.isEmpty()){
                    try{
                        emailService.sendEmailNotification(currentSchedule.getUser().getEmail(), currentSchedule.getName());
                        Notification notification = new Notification(currentSchedule.getName(),currentSchedule.getTimeOfIntake(), currentSchedule, LocalDateTime.now());
                        //Retrieve the active user ID and insert it to the new notification entry
                        ActiveUser activeUser = activeUserRepository.findByActiveStatus();
                        notification.setUserId(activeUser.getUserId());
                        notificationRepository.save(notification);
                        log.info("Email notification sent for: {} at {}", currentSchedule.getName(), LocalDateTime.now());
                    }
                    //To be updated
                    catch(Exception e){
                        log.error("An error occurred : {}", e.getMessage());
                    }
                }
                else {
                    log.debug("A notification was already sent for: {}", currentSchedule.getName());
                }

            }
        });
    }

    public void setSchedulesForToday(List<Schedule> schedulesForToday) {
        this.schedulesForToday = schedulesForToday;
    }

    //Logic responsible for triggering the NodeMCU buzzer
    @Transactional
    @Scheduled(cron = "0 * * * * *")  // Runs at exactly 0 seconds of every minute (e.g., 09:31:00, 09:32:00)
    public void triggerNodeBuzzer() {

        log.debug("Buzzer trigger check running...");
        /**
         * The schedule time doesn't include seconds
         */


        //Check if current schedules is not empty
        if (schedulesForToday == null || schedulesForToday.isEmpty()) {
            log.debug("No schedules for today, skipping buzzer check");
            return;
        }

        LocalDateTime currentDate = LocalDateTime.now();

        // Collect schedule IDs that need buzzer trigger to avoid detached entity issues
        List<Integer> scheduleIdsToUpdate = new ArrayList<>();
        
        for (Schedule schedule : schedulesForToday) {
            // Re-fetch from database to ensure we have the latest state
            Schedule currentSchedule = scheduleRepository.findById(schedule.getId())
                    .orElse(null);
            
            if (currentSchedule == null) {
                log.warn("Schedule with ID {} not found in database", schedule.getId());
                continue;
            }

            if (currentDate.truncatedTo(ChronoUnit.MINUTES).equals(currentSchedule.getTimeOfIntake().truncatedTo(ChronoUnit.MINUTES))){
                if (!currentSchedule.getBuzzerTriggered()) {
                    try{
                        //Tell Node to trigger the buzzer
                        nodeMCUService.triggerBuzzer(currentSchedule.getName());
                        currentSchedule.setBuzzerTriggered(true);
                        scheduleRepository.save(currentSchedule);
                        scheduleIdsToUpdate.add(currentSchedule.getId());

                        log.info("Buzzer triggered for: {} at {}",
                                currentSchedule.getName(),
                                currentSchedule.getTimeOfIntake().toLocalTime());
                    }catch (Exception e) {
                        log.error("Failed to trigger NodeMCU buzzer: {}", e.getMessage());
                    }
                }
                else {
                    log.debug("Buzzer already triggered for: {}", currentSchedule.getName());
                }
            }
        }
        
        // Force flush to ensure database is updated
        if (!scheduleIdsToUpdate.isEmpty()) {
            entityManager.flush();
            log.debug("Flushed {} buzzer updates to database", scheduleIdsToUpdate.size());
        }
    }

    //Auto update the done value of a schedule for today based on intake table


}
