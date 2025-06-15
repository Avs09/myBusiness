package com.myBusiness.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConsistentThresholdValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsistentThreshold {
    String message() default "thresholdMin must be less than or equal to thresholdMax";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
