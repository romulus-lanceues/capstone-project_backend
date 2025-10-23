package com.mediciationbox.capstone.medication_app.dto;

import java.util.Map;

public class ResponseDTO {
    private boolean status;
    private String message;
    private Map<String, Object> details;

    public ResponseDTO(boolean status, String message, Map<String, Object> details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "details=" + details +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}   
