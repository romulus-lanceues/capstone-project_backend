package com.mediciationbox.capstone.medication_app.exception;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message){
        super(message);
    }
}
