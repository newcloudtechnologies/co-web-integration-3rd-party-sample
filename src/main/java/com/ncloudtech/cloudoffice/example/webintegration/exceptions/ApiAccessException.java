package com.ncloudtech.cloudoffice.example.webintegration.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class ApiAccessException extends RuntimeException {

    private static final long serialVersionUID = -4769545814845198279L;

    @Getter
    private HttpStatus status;

    public ApiAccessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}

