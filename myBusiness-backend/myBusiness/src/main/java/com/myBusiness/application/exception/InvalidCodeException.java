package com.myBusiness.application.exception;

public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException(String msg) {
        super(msg);
    }
}
