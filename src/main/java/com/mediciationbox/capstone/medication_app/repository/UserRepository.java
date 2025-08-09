package com.mediciationbox.capstone.medication_app.repository;

import com.mediciationbox.capstone.medication_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
