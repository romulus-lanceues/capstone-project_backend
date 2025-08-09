package com.mediciationbox.capstone.medication_app.exception;

public class NoExistingAccountException extends RuntimeException{
    public NoExistingAccountException(String message){
        super(message);
    }
}
