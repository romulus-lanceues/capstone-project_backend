package com.mediciationbox.capstone.medication_app.exception;

public class EmptyIntakeTableException extends RuntimeException{
    public EmptyIntakeTableException(String message){
        super(message);
    }
}
