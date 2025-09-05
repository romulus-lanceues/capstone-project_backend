package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.ActiveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActiveUserRepository extends JpaRepository<ActiveUser, Integer> {
    ActiveUser findByUserId(Long id);

    @Query("SELECT s  FROM ActiveUser s WHERE s.active = true")
    ActiveUser findByActiveStatus();
    
}
