package com.mediciationbox.capstone.medication_app.service;

import com.mediciationbox.capstone.medication_app.dto.AddScheduleDTO;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private UserRepository userRepository;

    public ScheduleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Schedule
    createSchedule(AddScheduleDTO addScheduleDTO){

        Schedule schedule = new Schedule(addScheduleDTO.name(), addScheduleDTO.timeOfIntake(), addScheduleDTO.frequency(), addScheduleDTO.duration(), addScheduleDTO.notes(),false);
        schedule.setUser(userRepository.findByEmail(addScheduleDTO.userEmail()));

        return schedule;
    }

    public List<Schedule> retrieveScheduleForToday(Long id){
        Optional<User> account = userRepository.findById(id);

        //Add exception if user ID is invalid for some reason

        List<Schedule> allSchedule = account.get().getUserSchedule();

        LocalDateTime currentDate = LocalDateTime.now();
        List<Schedule> allScheduleForToday = new ArrayList<>();

        for(Schedule schedule : allSchedule){
            if(schedule.getTimeOfIntake().getDayOfMonth() == currentDate.getDayOfMonth()){
                allScheduleForToday.add(schedule);
            }
        }

        return allScheduleForToday;
    }

    public List<Schedule> retrieveHistory(Long id){
        Optional<User> account = userRepository.findById(id);

        List<Schedule> allSchedule = account.get().getUserSchedule();
        List<Schedule> history = new ArrayList<>();

        LocalDateTime historyStart = LocalDateTime.now().minusDays(7);
        LocalDateTime historyEndPoint = LocalDateTime.now().minusDays(1);

        for(Schedule schedule : allSchedule){
            if(schedule.getTimeOfIntake().getDayOfYear() >= historyStart.getDayOfYear() && schedule.getTimeOfIntake().getDayOfYear() <= historyEndPoint.getDayOfYear()){
                history.add(schedule);
            }
        }

        return history;
    }
}
