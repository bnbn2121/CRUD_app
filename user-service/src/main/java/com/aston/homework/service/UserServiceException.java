package com.aston.homework.service;

public class UserServiceException extends RuntimeException {
    public UserServiceException() {
    }

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
