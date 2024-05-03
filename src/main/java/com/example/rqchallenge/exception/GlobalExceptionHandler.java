package com.example.rqchallenge.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorDetails> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        logger.error("HTTP error occurred: {}", ex.getStatusCode(), ex);
        ErrorDetails errorDetails = new ErrorDetails(ex.getStatusCode(), List.of((Objects.requireNonNull(ex.getMessage()))));
        return ResponseEntity.status(ex.getStatusCode()).body(errorDetails);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorDetails> handleRestClientException(RestClientException ex) {
        logger.error("Rest client error occurred", ex);
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, List.of((Objects.requireNonNull(ex.getMessage()))));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(Exception ex) {
        logger.error("Unknown error occurred", ex);
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, List.of((Objects.requireNonNull(ex.getMessage()))));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    @ExceptionHandler(InvalidEmployeeDataException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmployeeDataException(InvalidEmployeeDataException ex) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, List.of((Objects.requireNonNull(ex.getMessage()))));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, List.of((Objects.requireNonNull(ex.getMessage()))));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MismatchedInputException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorDetails> handleInvalidInputException(Exception ex) {
        logger.error("Provided input is either empty or not in expected format or data type", ex);
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, List.of(("Provided input is either empty or not in expected format or data type"), Objects.requireNonNull(ex.getMessage())));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
