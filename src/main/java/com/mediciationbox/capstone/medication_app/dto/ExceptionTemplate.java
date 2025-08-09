package com.mediciationbox.capstone.medication_app.dto;

import java.time.LocalDateTime;

//All Exception will have this return template for consistency

public class ExceptionTemplate {
    private LocalDateTime errorTime;
    private boolean success;
    private String message;
    private String path;

    public ExceptionTemplate(LocalDateTime errorTime, boolean success, String message, String path) {
        this.errorTime = errorTime;
        this.success = success;
        this.message = message;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ExceptionTemplate{" +
                "errorTime=" + errorTime +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
