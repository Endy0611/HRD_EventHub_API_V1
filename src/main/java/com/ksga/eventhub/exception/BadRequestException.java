package com.ksga.eventhub.exception;

import lombok.Getter;

import java.util.Map;
@Getter
public class BadRequestException extends RuntimeException {
    private Map<String, String> errors;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(Map<String, String> errors) {
        super("Bad Request");
        this.errors = errors;
    }

    public BadRequestException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}
