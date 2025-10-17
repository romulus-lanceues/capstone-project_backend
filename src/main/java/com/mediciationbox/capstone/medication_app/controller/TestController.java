package com.mediciationbox.capstone.medication_app.controller;

import com.mediciationbox.capstone.medication_app.exception.NoExistingScheduleException;
import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.repository.ScheduleRepository;
import com.mediciationbox.capstone.medication_app.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * End points that are used to test features and fix errors
 */

@RestController
public class TestController {
    private UserRepository userRepository;
    private ScheduleRepository scheduleRepository;

    public TestController(UserRepository userRepository, ScheduleRepository scheduleRepository){
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @PostMapping("/api/test/update-trigger-value/{id}")
    public ResponseEntity<?> updateTriggerValueOfATask(@PathVariable Integer id){
        Optional<Schedule> selectedSchedule = scheduleRepository.findById(id);

        //Throws an error if no schedule was found
        Schedule schedule = selectedSchedule.orElseThrow(() -> new NoExistingScheduleException("No existing schedule for the schedule id + " + id));

        //Continue the logic if it isn't empty
        schedule.setBuzzerTriggered(true);
        scheduleRepository.save(schedule);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
