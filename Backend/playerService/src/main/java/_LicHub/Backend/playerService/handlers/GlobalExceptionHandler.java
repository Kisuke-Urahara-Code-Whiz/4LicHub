package _LicHub.Backend.playerService.handlers;

import _LicHub.Backend.playerService.dtos.ErrorResponse;
import _LicHub.Backend.playerService.exceptions.InvalidCredentialsException;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntry(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(false, "Signup failed: Username or Email is already taken.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralExceptions(Exception ex) {
        System.err.println("Unexpected Error: " + ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(false, "An unexpected internal server error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().getFirst();
        String errorMessage = firstError.getDefaultMessage();
        ErrorResponse errorResponse = new ErrorResponse(false, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(false, ex.getMessage()));
    }
}