// src/main/java/com/myBusiness/application/exception/MovementNotFoundException.java
package com.myBusiness.application.exception;

public class MovementNotFoundException extends RuntimeException {
    public MovementNotFoundException(String string) {
        super("Inventory movement not found with ID: " + string);
    }
}
