package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.dto.AddScheduleDTO;
import com.mediciationbox.capstone.medication_app.dto.ResponseDTO;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.model.User;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import com.mediciationbox.capstone.medication_app.service.JWTService;
import com.mediciationbox.capstone.medication_app.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//        jwtService.checkIfTokenIsEmpty(authHeader); //Throws an exception if not valid
//
//        //Throws a runtime exception if an error is encountered
//        jwtService.validateToken(authHeader);

        Schedule schedule = scheduleService.createSchedule(addScheduleDTO);

        scheduleRepository.save(schedule);

        Map<String, Object> scheduleDetails = new HashMap<>();
        scheduleDetails.put("name", schedule.getName());
        scheduleDetails.put("time", schedule.getTimeOfIntake());
        ResponseDTO responseDTO = new ResponseDTO(true, "Success",scheduleDetails);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

    }
    //Get all schedules
    @GetMapping("/api/schedule")
    public List<Schedule> getSchedules(){
        return  scheduleRepository.findAll();
    }


    //Get all the schedule from a specific user
    @GetMapping("/api/schedule/{id}")
    public List<Schedule> getScheduleByUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){

        jwtService.checkIfTokenIsEmpty(authHeader); //Throws an exception if not valid

        //Throws a runtime exception if an error is encountered
        jwtService.validateToken(authHeader);

        Optional<User> account = userRepository.findById(id);
        return account.get().getUserSchedule();
    }

    //Get all that's scheduled for today --    Home Tab
    @GetMapping("/api/schedule_today/{id}")
    public List<Schedule> getScheduleToday(@PathVariable Long id){
        return scheduleService.retrieveScheduleForToday(id);
    }


    //Get - History of the medication for a week ---History
    @GetMapping("api/weekly_history/{id}")
    public List<Schedule> getHistory(@PathVariable Long id){
        return scheduleService.retrieveHistory(id);
    }

}
