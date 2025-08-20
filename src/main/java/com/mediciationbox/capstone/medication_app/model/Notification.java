package com.mediciationbox.capstone.medication_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime scheduledTime;

    //Implementing an ENUM
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Integer retryCount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Schedule schedule;

    public Notification() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public String getScheduleName() {
        return schedule != null ? schedule.getName() : null;
    }


    public LocalDateTime getTimeOfIntake() {
        return schedule != null ? schedule.getTimeOfIntake() : null;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", scheduledTime=" + scheduledTime +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", user=" + user +
                ", schedule=" + schedule +
                '}';
    }

}


