package com.example.rqchallenge.exception;

// Custom exception class for handling employee not found scenario
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
