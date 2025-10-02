package com.ngocduy.fap.swp391.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleBadException(MethodArgumentNotValidException exception) {
        String message = "";

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            message += fieldError.getField() + ": " + fieldError.getDefaultMessage()+"\n";
        }

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handleBadCredentialsException(BadCredentialsException exception) {

        return ResponseEntity.status(401).body(exception.getMessage());
    }



}
