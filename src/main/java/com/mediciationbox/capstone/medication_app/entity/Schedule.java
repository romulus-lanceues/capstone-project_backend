package com.mediciationbox.capstone.medication_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mediciationbox.capstone.medication_app.model.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime timeOfIntake;

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false)
    private Integer duration;

    @Column
    private String notes;

    @Column
    private boolean done = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule parentSchedule;

    @OneToMany(mappedBy = "parentSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Schedule> childSchedules;

    @OneToMany(mappedBy = "parentScheduleForNotif", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @org.hibernate.annotations.BatchSize(size = 20)  // Fetch notifications in batches to avoid N+1
    private List<Notification> notifications;

    private boolean isGenerated = false;

    @Column(nullable = false)
    private Boolean buzzerTriggered = false;

    public Schedule(){

    }

    public Schedule(Integer id, String name, LocalDateTime timeOfIntake, String frequency, Integer duration, String notes, boolean done) {
        this.id = id;
        this.name = name;
        this.timeOfIntake = timeOfIntake;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
        this.done = done;
    }

    public Schedule(String name, LocalDateTime timeOfIntake, String frequency, Integer duration, String notes, boolean done) {

        this.name = name;
        this.timeOfIntake = timeOfIntake;
        this.frequency = frequency;
        this.duration = duration;
        this.notes = notes;
        this.done = done;
    }

    //Used for creation of child schedules
    public Schedule(Schedule parentSchedule, LocalDateTime newTimeOfIntake) {
        this.name = parentSchedule.getName();
        this.timeOfIntake = newTimeOfIntake;
        this.frequency = parentSchedule.getFrequency();
        this.duration = parentSchedule.getDuration();
        this.notes = parentSchedule.getNotes();
        this.done = false;
        this.parentSchedule = parentSchedule;
        this.isGenerated = true;
        this.user = parentSchedule.getUser();
    }


    public Boolean getBuzzerTriggered() {
        return buzzerTriggered;
    }

    public void setBuzzerTriggered(Boolean buzzerTriggered) {
        this.buzzerTriggered = buzzerTriggered;
    }

    public List<Schedule> getChildSchedules() {
        return childSchedules;
    }

    public void setChildSchedules(List<Schedule> childSchedules) {
        this.childSchedules = childSchedules;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean generated) {
        isGenerated = generated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Schedule getParentSchedule() {
        return parentSchedule;
    }

    public void setParentSchedule(Schedule parentSchedule) {
        this.parentSchedule = parentSchedule;
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
}
