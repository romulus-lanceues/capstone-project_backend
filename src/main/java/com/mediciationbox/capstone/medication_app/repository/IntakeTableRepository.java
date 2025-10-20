package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.IntakeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntakeTableRepository extends JpaRepository<IntakeTable, Integer> {
    Optional<IntakeTable> findFirstByOrderByIdDesc();
}
