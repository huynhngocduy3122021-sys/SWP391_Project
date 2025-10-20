package com.ngocduy.fap.swp391.exception;


import com.ngocduy.fap.swp391.exception.exceptions.AuctionException;
import com.ngocduy.fap.swp391.exception.exceptions.AuthenticationException;
import com.ngocduy.fap.swp391.exception.exceptions.BIDException;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
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

        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity handleInternalAuthenticationServiceException(InternalAuthenticationServiceException exception) {
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(AuthenticationException exception) {
        return ResponseEntity.status(401).body(exception.getMessage());
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(404).body(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleEntityNotFoundException(EntityNotFoundException exception) {
        return ResponseEntity.status(404).body(exception.getMessage());
    }


    @ExceptionHandler(BIDException.class)
    public ResponseEntity handleBidException(BIDException exception) {
        return ResponseEntity.status(401).body(exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String message = "Database constraint violation";
        if (exception.getMessage().contains("email")) {
            message = "Email already exists";
        } else if (exception.getMessage().contains("phone")) {
            message = "Phone number already exists";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    @ExceptionHandler(AuctionException.class)
    public ResponseEntity handleAuctionException(AuctionException exception) {
        return ResponseEntity.status(401).body(exception.getMessage());
    }



}
