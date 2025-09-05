package com.mediciationbox.capstone.medication_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ActiveUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String email;

    Long userId;

    boolean active;

    public ActiveUser(){

    }

    public ActiveUser( String email, Long userId, boolean active) {
        this.email = email;
        this.userId = userId;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "ActiveUser{" +
                "active=" + active +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", userId=" + userId +
                '}';
    }
}
