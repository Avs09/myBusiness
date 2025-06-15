// src/main/java/com/myBusiness/application/validation/ConsistentThresholdValidator.java
package com.myBusiness.application.validation;

import com.myBusiness.application.dto.ProductInputDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConsistentThresholdValidator
    implements ConstraintValidator<ConsistentThreshold, ProductInputDto> {

    @Override
    public boolean isValid(ProductInputDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        if (dto.getThresholdMin() > dto.getThresholdMax()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "thresholdMin cannot be greater than thresholdMax")
                .addPropertyNode("thresholdMin")
                .addConstraintViolation();
            return false;
        }
        return true;
    }
}
