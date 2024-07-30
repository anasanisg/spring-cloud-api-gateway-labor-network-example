package com.tooltracker.backend.walltools.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ToolStatusValidator.class })
public @interface ValidToolStatus {

    String message() default "Try to set invalid status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
