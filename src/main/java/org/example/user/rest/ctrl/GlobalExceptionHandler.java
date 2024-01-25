package org.example.user.rest.ctrl;

import lombok.extern.slf4j.Slf4j;
import org.example.user.app.exception.*;
import org.example.user.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {UserAppAccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccess(UserAppAccessDeniedException ex, WebRequest request) {
        log.error("exception handler called", ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.from(List.of(ex.getMessage())));
    }

    @ExceptionHandler(value = {UserAppValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidation(UserAppValidationException ex, WebRequest request) {
        log.error("exception handler called", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(List.of(ex.getMessage())));
    }

    @ExceptionHandler(value = {UserAppNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(UserAppNotFoundException ex, WebRequest request) {
        log.error("exception handler called", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.from(List.of(ex.getMessage())));
    }

    @ExceptionHandler(value = {UserAppIllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegalState(UserAppIllegalStateException ex, WebRequest request) {
        log.error("exception handler called", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(List.of(ex.getMessage())));
    }


    @ExceptionHandler(value = {UserAppException.class})
    public ResponseEntity<ErrorResponse> handleOtherDomain(UserAppException ex, WebRequest request) {
        log.error("exception handler called", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(ErrorResponse.from(List.of(ex.getMessage())));
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleOther(RuntimeException ex, WebRequest request) {
        log.error("exception handler called", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(List.of(ex.getMessage())));
    }
}