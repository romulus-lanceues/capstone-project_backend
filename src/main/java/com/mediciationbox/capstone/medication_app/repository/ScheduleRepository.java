package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.Schedule;
import com.mediciationbox.capstone.medication_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    boolean existsByParentScheduleAndTimeOfIntakeBetween(
            Schedule parentSchedule, LocalDateTime startOfDay, LocalDateTime endOfDay
    );

    @Query("SELECT s FROM Schedule s WHERE s.parentSchedule.id IN :parentIds AND s.timeOfIntake BETWEEN :startOfDay AND :endOfDay")
    List<Schedule> findByParentScheduleIdInAndTimeOfIntakeBetween(
            @Param("parentIds") List<Integer> parentIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
