package com.mediciationbox.capstone.medication_app.exception;


import com.mediciationbox.capstone.medication_app.dto.ExceptionTemplate;
import com.mediciationbox.capstone.medication_app.dto.ValidationExceptionError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
            (MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        //Get the Validation Errors
        List<FieldError> fieldErrors = ex.getFieldErrors();

        Map<String, String> errors = new HashMap<>();
        for(FieldError fieldError : fieldErrors){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ValidationExceptionError validationExceptionError =
                new ValidationExceptionError(LocalDateTime.now(), errors, "Bad Request");

        return new ResponseEntity<>(validationExceptionError ,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ExceptionTemplate>
    accountAlreadyExists(Exception ex, HttpServletRequest httpServletRequest){

        ExceptionTemplate exception = new ExceptionTemplate(LocalDateTime.now(),
                false, ex.getMessage(), httpServletRequest.getRequestURI());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoExistingAccountException.class, NoExistingScheduleException.class})
    public ResponseEntity<ExceptionTemplate>
        noExistingAccount(Exception ex, HttpServletRequest httpServletRequest){

        ExceptionTemplate exception = new ExceptionTemplate(LocalDateTime.now(),
                false, ex.getMessage(), httpServletRequest.getRequestURI());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ExceptionTemplate>
        wrongPassword(Exception ex, HttpServletRequest httpServletRequest){

        ExceptionTemplate exception = new ExceptionTemplate(LocalDateTime.now(), false, ex.getMessage(), httpServletRequest.getRequestURI());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionTemplate>
        invalidToken(Exception ex, HttpServletRequest httpServletRequest){

        ExceptionTemplate exception = new ExceptionTemplate(LocalDateTime.now(), false, ex.getMessage(), httpServletRequest.getRequestURI());
        
        return new ResponseEntity<>(exception, HttpStatus.UNAUTHORIZED);
    }
    

}
