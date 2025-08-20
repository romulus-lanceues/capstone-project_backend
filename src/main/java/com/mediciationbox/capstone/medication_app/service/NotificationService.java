package com.mediciationbox.capstone.medication_app.service;

import com.mediciationbox.capstone.medication_app.exception.NoExistingScheduleException;
import com.mediciationbox.capstone.medication_app.model.Notification;
import com.mediciationbox.capstone.medication_app.model.NotificationStatus;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.repository.NotificationRepository;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    public NotificationRepository notificationRepository;
    public ScheduleRepository scheduleRepository;

    public NotificationService
            (NotificationRepository notificationRepository, ScheduleRepository scheduleRepository) {
        this.notificationRepository = notificationRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public void createNotification(Integer scheduleId){
        //Check if the schedule exists
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);

        Schedule schedule =
                optionalSchedule.orElseThrow( () -> new NoExistingScheduleException("No existing schedule found"));

        Notification notification = new Notification();
        notification.setSchedule(schedule);
        notification.setUser(schedule.getUser());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);
        notification.setScheduledTime(schedule.getTimeOfIntake().minusMinutes(2));

        switch (notification.getSchedule().getFrequency()){
            case "daily" :
                //generateDailyNotifs()
                System.out.println("This schedule is daily");
                break;
            case "twice" :
                //twiceDailyNotifs()
                System.out.println("This schedule is twice a day");
                break;
            case "three" :
                //threeDailyNotifs()
                System.out.println("This schedule is thrice a day");
            case "weekly" :
                //weeklyNotifs()
                System.out.println("This schedule is weekly");
        }

//        notificationRepository.save(notification);

    }

    public 
}
