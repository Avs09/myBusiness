package com.myBusiness.application.exception;

public class UnitNotFoundException extends RuntimeException {
    public UnitNotFoundException(Long id) {
        super("Unit not found with ID: " + id);
    }
}