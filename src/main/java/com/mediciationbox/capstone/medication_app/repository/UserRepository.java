package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    // Optimized query to fetch user with schedules in one query (fixes N+1 problem)
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userSchedule WHERE u.id = :id")
    Optional<User> findByIdWithSchedules(@Param("id") Long id);
}
