package com.mediciationbox.capstone.medication_app.config;



import com.mediciationbox.capstone.medication_app.dto.SchedulesForTodayEvent;
import com.mediciationbox.capstone.medication_app.model.ActiveUser;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.repository.ActiveUserRepository;
import com.mediciationbox.capstone.medication_app.service.EmailService;
import com.mediciationbox.capstone.medication_app.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    private final ScheduleService scheduleService;
    private final ActiveUserRepository activeUserRepository;
    private final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final EmailService emailService;
    private List<Schedule> schedulesForToday;


    public ScheduledTasks(ScheduleService scheduleService, ActiveUserRepository activeUserRepository, EmailService emailService){
        this.scheduleService = scheduleService;
        this.activeUserRepository = activeUserRepository;
        this.emailService = emailService;
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


    @Scheduled(fixedDelay = 10000)
    public void sendNotification(){
        if(schedulesForToday == null || schedulesForToday.isEmpty()){
            return;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        schedulesForToday.stream().forEach( schedule -> {
            LocalDateTime scheduleTime = schedule.getTimeOfIntake();

            //FIX THIS LOGIC ASAP
            if(currentTime.equals(scheduleTime.minusMinutes(2)) || (currentTime.isAfter(scheduleTime.minusMinutes(2))) && currentTime.isBefore(scheduleTime)){
                try{
                    emailService.sendEmailNotification(schedule.getUser().getEmail(), schedule.getName());
                }
                //To be updated
                catch(Exception e){
                    log.error("An error occurred : {}", e.getMessage());
                }
            }
        });
    }

    public void setSchedulesForToday(List<Schedule> schedulesForToday) {
        this.schedulesForToday = schedulesForToday;
    }
}
