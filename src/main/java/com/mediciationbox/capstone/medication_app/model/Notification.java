package com.mediciationbox.capstone.medication_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String notificationName;
    private LocalDateTime scheduleTime;
    private LocalDateTime timeSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Schedule parentScheduleForNotif;

    public Notification (){
    }

    public Notification(String notificationName, LocalDateTime scheduleTime, Schedule parentScheduleForNotif, LocalDateTime timeSent) {
        this.notificationName = notificationName;
        this.scheduleTime = scheduleTime;
        this.parentScheduleForNotif = parentScheduleForNotif;
        this.timeSent = timeSent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public Schedule getParentScheduleForNotif() {
        return parentScheduleForNotif;
    }

    public void setParentScheduleForNotif(Schedule parentScheduleForNotif) {
        this.parentScheduleForNotif = parentScheduleForNotif;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }


    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(LocalDateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", notificationName='" + notificationName + '\'' +
                ", timeSent=" + timeSent +
                ", parentScheduleForNotif=" + parentScheduleForNotif +
                '}';
    }
}
