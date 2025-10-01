package com.mediciationbox.capstone.medication_app.service;

import com.mediciationbox.capstone.medication_app.config.ScheduledTasks;
import com.mediciationbox.capstone.medication_app.dto.AddScheduleDTO;
import com.mediciationbox.capstone.medication_app.dto.SchedulesForTodayEvent;
import com.mediciationbox.capstone.medication_app.exception.NoExistingAccountException;
import com.mediciationbox.capstone.medication_app.exception.NoExistingScheduleException;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    //Development Event Publisher
    private final ApplicationEventPublisher applicationEventPublisher;


    public ScheduleService(UserRepository userRepository, ScheduleRepository scheduleRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    public Schedule
    createSchedule(AddScheduleDTO addScheduleDTO){

        Schedule schedule = new Schedule(addScheduleDTO.name(), addScheduleDTO.timeOfIntake(), addScheduleDTO.frequency(), addScheduleDTO.duration(), addScheduleDTO.notes(),false);
        schedule.setUser(userRepository.findByEmail(addScheduleDTO.userEmail()));

        return schedule;
    }

    //
    public List<Schedule> retrieveScheduleForToday(Long id){
        //Find the user
        Optional<User> account = userRepository.findById(id);
        //Add exception if user ID is invalid for some reason
        User user = account.orElseThrow(() -> new NoExistingAccountException("Account doesn't exist"));

        //Will be used in the filtering
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDate today = currentDate.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        List<Schedule> allSchedule = user.getUserSchedule();
        // Filter to only get parent schedules (not generated ones)
        List<Schedule> parentSchedulesOnly = allSchedule.stream()
                .filter(schedule -> !schedule.isGenerated())
                .toList();

        //Filter parent schedules that are due today
        List<Schedule> parentsDueToday =  parentSchedulesOnly.stream().filter(schedule -> {
            LocalDateTime startingDate = schedule.getTimeOfIntake();
            //Switch
            switch (schedule.getFrequency()){
                case "daily":
                    //This logic is under testing
                    LocalDateTime scheduleEndForDaily = schedule.getTimeOfIntake().plusDays(schedule.getDuration());
                    return !currentDate.toLocalDate().isBefore(startingDate.toLocalDate()) && !currentDate.toLocalDate().isAfter(scheduleEndForDaily.toLocalDate());

                case "weekly":
                    LocalDateTime scheduleEnd = startingDate.plusWeeks(schedule.getDuration());

                    // Check if current date is within the schedule period
                    if (currentDate.toLocalDate().isBefore(startingDate.toLocalDate()) || currentDate.toLocalDate().isAfter(scheduleEnd.toLocalDate())) {
                        return false;
                    }
                    // Calculate how many days have passed since the start
                    long daysBetween = ChronoUnit.DAYS.between(startingDate.toLocalDate(), currentDate.toLocalDate());

                    // Check if it's exactly a weekly interval (0, 7, 14, 21 days)
                    return daysBetween % 7 == 0;
                default:
                    return false;
            }
        }).toList();



        List<Schedule> scheduleForToday = new ArrayList<>();

        //Get Parent Id's
        List<Integer> parentsIds = parentsDueToday.stream().map(Schedule::getId).toList();

        List<Schedule> allChildrenForToday = scheduleRepository.findByParentScheduleIdInAndTimeOfIntakeBetween(parentsIds, startOfDay, endOfDay);

        Map<Integer, List<Schedule>> childByParentId = allChildrenForToday.stream().collect(Collectors.groupingBy(child -> child.getParentSchedule().getId()));

        List<Schedule> createdSchedules = new ArrayList<>();
        for(Schedule parent : parentsDueToday){

            if(!parent.getTimeOfIntake().isBefore(startOfDay) && !parent.getTimeOfIntake().isAfter(endOfDay) ){
                scheduleForToday.add(parent);
                continue;
            }

            List<Schedule> existingChildren = childByParentId.getOrDefault(parent.getId(), Collections.emptyList());
            if(existingChildren.isEmpty()){
                LocalDateTime newScheduleTime = today.atTime(parent.getTimeOfIntake().toLocalTime());
                Schedule newSchedule = new Schedule(parent, newScheduleTime);
                createdSchedules.add(newSchedule);
                scheduleForToday.add(newSchedule);
            }
            else {
                scheduleForToday.addAll(existingChildren);
            }

        }
        scheduleRepository.saveAll(createdSchedules);

        //Create an event for the event listeners to listen to
        applicationEventPublisher.publishEvent( new SchedulesForTodayEvent(scheduleForToday, LocalDate.now()));

        return sortScheduledTime(scheduleForToday);

    }
    //Supporting method that checks of the schedule already exists
    public boolean checkIfTheScheduleAlreadyExists(Schedule parentSchedule, LocalDateTime currentDate) {
        LocalDate today = currentDate.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        return scheduleRepository.existsByParentScheduleAndTimeOfIntakeBetween(
                parentSchedule, startOfDay, endOfDay
        );
    }


    public List<Schedule> retrieveHistory(Long id){
        Optional<User> account = userRepository.findById(id);

        List<Schedule> allSchedule = account.get().getUserSchedule();


        LocalDate historyStart = LocalDateTime.now().toLocalDate().minusDays(7);
        LocalDate historyEndPoint = LocalDateTime.now().toLocalDate().minusDays(1);


//        List<Schedule> history = allSchedule.stream().filter(schedule -> {
//            LocalDateTime scheduleDate = schedule.getTimeOfIntake();
//            return !scheduleDate.isBefore(historyStart) && !scheduleDate.isAfter(historyEndPoint);
//        }).collect(Collectors.toList());

        List<Schedule> history = allSchedule.stream().filter(schedule -> {
             return !schedule.getTimeOfIntake().toLocalDate().isBefore(historyStart) && !schedule.getTimeOfIntake().toLocalDate().isAfter(historyEndPoint);
        }).collect(Collectors.toList());


        return sortByDateAndTime(history);
    }


    //Supporting methods

    private List<Schedule> sortScheduledTime(List<Schedule> scheduleForToday){

        //Listed schedule are the ones stored in this array already
        List<Schedule> sortedScheduleList = new ArrayList<>(scheduleForToday);

        sortedScheduleList.sort(Comparator.comparing(Schedule::getTimeOfIntake));

        return sortedScheduleList;
    }

    //Supporting Method that sorts the history by date and time for the history controller
    private List<Schedule> sortByDateAndTime(List<Schedule> historySchedules){

        List<Schedule> sortedByDate = new ArrayList<>(historySchedules);

        sortedByDate.sort(Comparator.comparing(Schedule::getTimeOfIntake).reversed());


        return sortedByDate;
    }

    public Map<LocalDate, List<Schedule>> sortByDay (List<Schedule> sortedSchedule){
        Map<LocalDate, List<Schedule>> history = new LinkedHashMap<>();

        for(Schedule schedule : sortedSchedule){
            LocalDate date = schedule.getTimeOfIntake().toLocalDate();
            history.computeIfAbsent(date, currentSchedule -> new ArrayList<>()).add(schedule);
        }

        return  history;
    }

    public void updateScheduleStatus(Integer scheduleId, Long userId){
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()){
            throw new NoExistingAccountException("No account found");
        }
        List<Schedule> userSchedule = user.get().getUserSchedule();

        Optional<Schedule> s = userSchedule.stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findFirst(); //Map expects a value back

        Schedule schedule = s.orElseThrow( () -> new NoExistingAccountException("message")); //.setDone(true);

        schedule.setDone(true);
        scheduleRepository.save(schedule);

    }

    public void deleteSchedule(Integer scheduleId){

        //Check if the schedule is valid
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        Schedule validSchedule = optionalSchedule.orElseThrow( () -> new NoExistingScheduleException("Schedule doesn't exist"));

        scheduleRepository.delete(validSchedule);
    }
}
