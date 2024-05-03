package com.example.rqchallenge.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ErrorDetails {

    private HttpStatus code;

    private List<String> message;

    public ErrorDetails(HttpStatus code, List<String> message) {
        this.code = code;
        this.message = message;
    }

    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }
}