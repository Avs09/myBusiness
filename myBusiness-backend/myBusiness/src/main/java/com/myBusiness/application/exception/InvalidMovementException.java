// src/main/java/com/myBusiness/application/exception/InvalidMovementException.java
package com.myBusiness.application.exception;

public class InvalidMovementException extends RuntimeException {
    public InvalidMovementException(String message) {
        super(message);
    }
}
