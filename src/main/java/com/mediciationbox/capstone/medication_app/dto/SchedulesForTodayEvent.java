package com.mediciationbox.capstone.medication_app.dto;


import com.mediciationbox.capstone.medication_app.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public class SchedulesForTodayEvent {
    private List<Schedule> schedulesForToday;
    private LocalDate dateForToday;

    public SchedulesForTodayEvent(List<Schedule> schedulesForToday, LocalDate dateForToday) {
        this.schedulesForToday = schedulesForToday;
        this.dateForToday = dateForToday;
    }

    public List<Schedule> getSchedulesForToday() {
        return schedulesForToday;
    }

    public void setSchedulesForToday(List<Schedule> schedulesForToday) {
        this.schedulesForToday = schedulesForToday;
    }

    public LocalDate getDateForToday() {
        return dateForToday;
    }

    public void setDateForToday(LocalDate dateForToday) {
        this.dateForToday = dateForToday;
    }
}
