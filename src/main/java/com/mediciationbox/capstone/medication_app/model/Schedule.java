package com.mediciationbox.capstone.medication_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private LocalDateTime timeOfIntake;
    private String frequency;
    private Integer duration;
    private String notes;
    private boolean done;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    public Schedule(){

    }

    //Typical constructor
    public Schedule(Integer id, String name, LocalDateTime timeOfIntake, String frequency, Integer duration, String notes, boolean done) {
        this.id = id;
        this.name = name;
        this.timeOfIntake = timeOfIntake;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
        this.done = done;
    }

    //Used for persistence
    public Schedule(String name, LocalDateTime timeOfIntake, String frequency, Integer duration, String notes, boolean done) {

        this.name = name;
        this.timeOfIntake = timeOfIntake;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "done=" + done +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", timeOfIntake=" + timeOfIntake +
                ", frequency='" + frequency + '\'' +
                ", duration=" + duration +
                ", notes='" + notes + '\'' +
                ", user=" + user +
                '}';
    }
}
