// src/main/java/com/myBusiness/application/exception/CategoryNotFoundException.java
package com.myBusiness.application.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("Category not found with ID: " + id);
    }
}
