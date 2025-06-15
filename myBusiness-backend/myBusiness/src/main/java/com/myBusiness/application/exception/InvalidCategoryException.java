// src/main/java/com/myBusiness/application/exception/InvalidCategoryException.java
package com.myBusiness.application.exception;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String message) {
        super(message);
    }
}
