package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.IntakeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeTableRepository extends JpaRepository<IntakeTable, Integer> {
}
