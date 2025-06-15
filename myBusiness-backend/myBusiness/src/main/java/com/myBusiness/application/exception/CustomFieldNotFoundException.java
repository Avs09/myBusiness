// src/main/java/com/myBusiness/application/exception/CustomFieldNotFoundException.java
package com.myBusiness.application.exception;

public class CustomFieldNotFoundException extends RuntimeException {
    public CustomFieldNotFoundException(Long id) {
        super("Custom field not found with ID: " + id);
    }
}
