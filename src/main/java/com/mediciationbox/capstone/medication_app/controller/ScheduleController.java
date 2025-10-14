package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.dto.AddScheduleDTO;
import com.mediciationbox.capstone.medication_app.dto.ResponseDTO;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import com.mediciationbox.capstone.medication_app.service.JWTService;
import com.mediciationbox.capstone.medication_app.service.ScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ScheduleController {
    private ScheduleRepository scheduleRepository;
    private ScheduleService scheduleService;
    private UserRepository userRepository;
    private JWTService jwtService;

    public ScheduleController
            (ScheduleService scheduleService, ScheduleRepository scheduleRepository, UserRepository userRepository, JWTService jwtService) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


    //Post - Uploads a schedule
    @PostMapping("/api/schedule-upload")
    public ResponseEntity<ResponseDTO>
    addSchedule(@RequestBody AddScheduleDTO addScheduleDTO, @RequestHeader("Authorization") String authHeader){

        // jwtService.checkIfTokenIsEmpty(authHeader); //Throws an exception if not valid

        // //Throws a runtime exception if an error is encountered
        // jwtService.validateToken(authHeader);

        Schedule schedule = scheduleService.createSchedule(addScheduleDTO);

        scheduleRepository.save(schedule);

        Map<String, Object> scheduleDetails = new HashMap<>();
        scheduleDetails.put("name", schedule.getName());
        scheduleDetails.put("time", schedule.getTimeOfIntake());
        ResponseDTO responseDTO = new ResponseDTO(true, "Success",scheduleDetails);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

    }
    //Get all schedules with pagination (for capstone project demo)
    @GetMapping("/api/schedule")
    public Page<Schedule> getSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timeOfIntake") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ){
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return scheduleRepository.findAll(pageable);
    }


    //Get all the schedule from a specific user
    @GetMapping("/api/schedule/{id}")
    public List<Schedule> getScheduleByUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){

        jwtService.checkIfTokenIsEmpty(authHeader); //Throws an exception if not valid

        //Throws a runtime exception if an error is encountered
        jwtService.validateToken(authHeader);

        // Use optimized query to avoid N+1 problem
        Optional<User> account = userRepository.findByIdWithSchedules(id);
        return account.get().getUserSchedule();
    }

    //Get all that's scheduled for today --    Home Tab
    @GetMapping("/api/schedule_today/{id}")
    public List<Schedule> getScheduleToday(@PathVariable Long id){
        return scheduleService.retrieveScheduleForToday(id);
    }


    //Get - History of the medication for a week -- History
    @GetMapping("api/weekly_history/{id}")
    public Map<LocalDate,List<Schedule>> getHistory(@PathVariable Long id){
        List<Schedule> sortedHistory = scheduleService.retrieveHistory(id);
        return scheduleService.sortByDay(sortedHistory);
    }

    //Update an entry to true
    @PatchMapping("api/update-schedule-to-done/{userId}/{scheduleId}")
    public ResponseEntity<ResponseDTO> updateAScheduleToDone(@PathVariable Long userId, @PathVariable Integer scheduleId){
        scheduleService.updateScheduleStatus(scheduleId, userId);
        ResponseDTO responseDTO = new ResponseDTO(true, "Success", Map.of("isDone", true));

           return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }

    @DeleteMapping("/api/drop-schedule/{scheduleId}")
    public ResponseEntity<ResponseDTO> deleteSchedule(@PathVariable Integer scheduleId){
        scheduleService.deleteSchedule(scheduleId);
        ResponseDTO responseDTO = new ResponseDTO(true, "Success", Map.of("isDeleted", true));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
