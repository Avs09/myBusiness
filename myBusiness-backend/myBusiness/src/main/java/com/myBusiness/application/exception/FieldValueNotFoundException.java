// src/main/java/com/myBusiness/application/exception/FieldValueNotFoundException.java
package com.myBusiness.application.exception;

public class FieldValueNotFoundException extends RuntimeException {
    public FieldValueNotFoundException(Long id) {
        super("Field value not found with ID: " + id);
    }
}
