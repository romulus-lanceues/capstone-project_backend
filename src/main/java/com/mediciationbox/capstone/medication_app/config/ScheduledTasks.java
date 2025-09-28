package com.mediciationbox.capstone.medication_app.config;



import com.mediciationbox.capstone.medication_app.dto.SchedulesForTodayEvent;
import com.mediciationbox.capstone.medication_app.model.ActiveUser;
import com.mediciationbox.capstone.medication_app.model.Notification;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.repository.ActiveUserRepository;
import com.mediciationbox.capstone.medication_app.repository.NotificationRepository;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import com.mediciationbox.capstone.medication_app.service.EmailService;
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
import java.util.List;

@Component
public class ScheduledTasks {
    private final ScheduleService scheduleService;
    private final ActiveUserRepository activeUserRepository;
    private final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private EntityManager entityManager;
    private List<Schedule> schedulesForToday;
    private final ScheduleRepository scheduleRepository;


    public ScheduledTasks(ScheduleService scheduleService,
                          ActiveUserRepository activeUserRepository, EmailService emailService,
                          NotificationRepository notificationRepository, EntityManager entityManager,
                          ScheduleRepository scheduleRepository){
        this.scheduleService = scheduleService;
        this.activeUserRepository = activeUserRepository;
        this.emailService = emailService;
        this.notificationRepository = notificationRepository;
        this.entityManager = entityManager;
        this.scheduleRepository = scheduleRepository;
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
    @Scheduled(fixedDelay = 10000)
    public void sendNotification(){
        if(schedulesForToday == null || schedulesForToday.isEmpty()){
            return;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        schedulesForToday.stream().forEach( schedule -> {

            /*
            Schedules are collected in a different class and there's no active hibernate connection here.
            Reattaching / Re-fetching is the appropriate solution.
            */

            Schedule currentSchedule = entityManager.merge(schedule); //Use this for consistency
            //Keep the hibernate connection open using @Transactional


            LocalDateTime scheduleTime = currentSchedule.getTimeOfIntake();

            //FIX THIS LOGIC ASAP
            if(currentTime.equals(scheduleTime.minusMinutes(2)) || (currentTime.isAfter(scheduleTime.minusMinutes(2))) && currentTime.isBefore(scheduleTime)){
                //Check if this schedule already produced a notification.
                List<Notification> notifications = currentSchedule.getNotifications();

                if(notifications.isEmpty()){
                    try{
                        emailService.sendEmailNotification(currentSchedule.getUser().getEmail(), currentSchedule.getName());
                        Notification notification = new Notification(currentSchedule.getName(),currentSchedule.getTimeOfIntake(), currentSchedule, LocalDateTime.now());
                        notificationRepository.save(notification);
                    }
                    //To be updated
                    catch(Exception e){
                        log.error("An error occurred : {}", e.getMessage());
                    }
                }
                else {
                    log.info("A notification was already sent");
                }

            }
        });
    }

    public void setSchedulesForToday(List<Schedule> schedulesForToday) {
        this.schedulesForToday = schedulesForToday;
    }

    //Logic responsible for triggering the NodeMCU buzzer
    @Transactional
    @Scheduled(fixedDelay = 30000) //Check every 30 seconds
    public void triggerNodeBuzzer() {

        //Check if current schedules is not empty
        if (schedulesForToday == null || schedulesForToday.isEmpty()) {
            return;
        }

        LocalDateTime currentDate = LocalDateTime.now();

        schedulesForToday.stream().forEach(schedule -> {

            Schedule currentSchedule = entityManager.merge(schedule);

            if (currentDate.toLocalTime().withSecond(0).equals(currentSchedule.getTimeOfIntake().toLocalTime())){
                if (!currentSchedule.getBuzzerTriggered()) {
                    //Tell Node to trigger the buzzer
                    currentSchedule.setBuzzerTriggered(true);
                    scheduleRepository.save(currentSchedule);

                }
            };
        });
    }
}
