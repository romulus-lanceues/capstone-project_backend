package com.mediciationbox.capstone.medication_app.exception;

public class NoActiveUserException extends RuntimeException{
    public NoActiveUserException(String message){
        super(message);
    }
}
