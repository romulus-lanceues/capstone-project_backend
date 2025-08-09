package com.mediciationbox.capstone.medication_app.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ValidationExceptionError {
    private LocalDateTime errorTime;
    private Map<String, String> validationErrors;
    private String message;


    public ValidationExceptionError(LocalDateTime errorTime, Map<String, String> validationErrors, String message) {
        this.errorTime = errorTime;
        this.validationErrors = validationErrors;
        this.message = message;
    }

    public LocalDateTime getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(LocalDateTime errorTime) {
        this.errorTime = errorTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String,String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @Override
    public String toString() {
        return "ValidationExceptionError{" +
                "errorTime=" + errorTime +
                ", validationErrors=" + validationErrors +
                ", message='" + message + '\'' +
                '}';
    }
}
