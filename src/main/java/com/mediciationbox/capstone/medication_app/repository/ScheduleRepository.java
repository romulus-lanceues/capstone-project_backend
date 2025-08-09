package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

}
