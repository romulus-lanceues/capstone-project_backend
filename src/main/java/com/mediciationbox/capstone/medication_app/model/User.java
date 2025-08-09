package com.mediciationbox.capstone.medication_app.model;

//Entity for the signup process

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Avoid Hibernate's allocation
    private Long id;

    @Email(message = "Enter a valid email address")
    @NotBlank(message = "Blank email detected")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 Characters Long")
    private String password;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Schedule> userSchedule;

    private String name;

    public User(){

    }

    public User(Long id, String email, String password, LocalDateTime createdAt, List<Schedule> userSchedule) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.userSchedule = userSchedule;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime localDateTime) {
        this.createdAt= localDateTime;
    }

    public List<Schedule> getUserSchedule() {
        return userSchedule;
    }

    public void setUserSchedule(List<Schedule> userSchedule) {
        this.userSchedule = userSchedule;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", id=" + id +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
