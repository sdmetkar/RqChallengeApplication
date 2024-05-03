package com.example.rqchallenge.exception;

// Custom exception class for handling invalid employee data
public class InvalidEmployeeDataException extends RuntimeException {
    public InvalidEmployeeDataException(String message) {
        super(message);
    }
}