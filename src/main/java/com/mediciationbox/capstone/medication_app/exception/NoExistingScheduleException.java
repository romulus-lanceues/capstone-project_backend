package com.mediciationbox.capstone.medication_app.exception;

public class NoExistingScheduleException extends RuntimeException{
    public NoExistingScheduleException(String message){
        super(message);
    }
}
