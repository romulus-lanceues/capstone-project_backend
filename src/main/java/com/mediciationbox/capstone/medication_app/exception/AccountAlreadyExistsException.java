package com.mediciationbox.capstone.medication_app.exception;

public class AccountAlreadyExistsException extends RuntimeException{
    public AccountAlreadyExistsException(String message){
        super(message);
    }
}
