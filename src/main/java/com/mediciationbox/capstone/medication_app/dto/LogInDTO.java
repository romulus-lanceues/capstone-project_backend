package com.mediciationbox.capstone.medication_app.dto;

public class LogInDTO {
    private String email;
    private String password;

    public LogInDTO(String email, String password) {
        this.email = email;
        this.password = password;
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

    @Override
    public String toString() {
        return "LogInTemplate{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
