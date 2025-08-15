package com.mediciationbox.capstone.medication_app.service;

import com.mediciationbox.capstone.medication_app.dto.AddScheduleDTO;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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

    //
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

        return sortScheduledTime(allScheduleForToday);

    }


    public List<Schedule> retrieveHistory(Long id){
        Optional<User> account = userRepository.findById(id);

        List<Schedule> allSchedule = account.get().getUserSchedule();


        LocalDateTime historyStart = LocalDateTime.now().minusDays(7);
        LocalDateTime historyEndPoint = LocalDateTime.now().minusDays(1);


//        List<Schedule> history = allSchedule.stream().filter(schedule -> {
//            LocalDateTime scheduleDate = schedule.getTimeOfIntake();
//            return !scheduleDate.isBefore(historyStart) && !scheduleDate.isAfter(historyEndPoint);
//        }).collect(Collectors.toList());

        List<Schedule> history = allSchedule.stream().filter(schedule -> {
            return !schedule.getTimeOfIntake().isBefore(historyStart) && !schedule.getTimeOfIntake().isAfter(historyEndPoint);
        }).collect(Collectors.toList());

        return sortByDateAndTime(history);
    }


    //Supporting methods

    private List<Schedule> sortScheduledTime(List<Schedule> scheduleForToday){

        //Listed schedule are the ones stored in this array already
        List<Schedule> sortedScheduleList = new ArrayList<>(scheduleForToday);

        sortedScheduleList.sort(Comparator.comparing(schedule -> schedule.getTimeOfIntake().toLocalTime()));

        return sortedScheduleList;
    }

    //Supporting Method that sorts the history by date and time for the history controller
    private List<Schedule> sortByDateAndTime(List<Schedule> historySchedules){
        List<Schedule> sortedByDate = new ArrayList<>(historySchedules);

        sortedByDate.sort(Comparator.comparing(schedule -> schedule.getTimeOfIntake().toLocalDate()));

        return sortScheduledTime(sortedByDate);
    }

    public Map<LocalDate, List<Schedule>> sortByDay (List<Schedule> sortedSchedule){

        Map<LocalDate, List<Schedule>> history = new HashMap<>();

        for(Schedule schedule : sortedSchedule){
            LocalDate date = schedule.getTimeOfIntake().toLocalDate();
            history.computeIfAbsent(date, currentSchedule -> new ArrayList<>()).add(schedule);
        }

        return  history;
    }
}
