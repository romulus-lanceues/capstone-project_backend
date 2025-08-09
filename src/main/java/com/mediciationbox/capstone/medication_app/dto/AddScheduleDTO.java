package com.mediciationbox.capstone.medication_app.dto;

import java.time.LocalDateTime;

public class AddScheduleDTO {

    private String name;
    private LocalDateTime timeOfIntake;
    private String frequency;
    private String userEmail;


    public AddScheduleDTO(String name, String frequency, LocalDateTime timeOfIntake,  String userEmail) {
        this.name = name;
        this.frequency = frequency;
        this.timeOfIntake = timeOfIntake;
        this.userEmail = userEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimeOfIntake() {
        return timeOfIntake;
    }

    public void setTimeOfIntake(LocalDateTime timeOfIntake) {
        this.timeOfIntake = timeOfIntake;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "AddScheduleDTO{" +
                "frequency='" + frequency + '\'' +
                ", name='" + name + '\'' +
                ", timeOfIntake=" + timeOfIntake +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
