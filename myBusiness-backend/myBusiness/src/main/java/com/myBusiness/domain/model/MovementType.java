// src/main/java/com/myBusiness/domain/model/MovementType.java
package com.myBusiness.domain.model;

public enum MovementType {
    ENTRY, EXIT, ADJUSTMENT;

    public boolean isEntry() {
        return this == ENTRY;
    }
}
