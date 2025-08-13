package com.mediciationbox.capstone.medication_app.dto;

import java.time.LocalDateTime;

public record AddScheduleDTO(
        String name,
        LocalDateTime timeOfIntake,
        String frequency,
        Integer duration,
        String notes,
        String userEmail) { }
