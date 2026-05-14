package com.epam.laboratory.app.exception;

public class DtoValidationException extends RuntimeException {
    public DtoValidationException(String message) {
        super(message);
    }
}
