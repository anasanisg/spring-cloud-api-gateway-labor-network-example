package com.tooltracker.backend.walltools.validators;

import com.tooltracker.backend.walltools.enums.ToolStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ToolStatusValidator implements ConstraintValidator<ValidToolStatus, ToolStatus> {

    // NOT IMPORTANT AT THIS STAGE, Just to make dealing with status easier in the
    // future
    @Override
    public boolean isValid(ToolStatus value, ConstraintValidatorContext context) {
        return (value == ToolStatus.GOOD || value == ToolStatus.TO_BE_REPLACED || value == ToolStatus.BROKEN
                || value == ToolStatus.NOT_SET
                || value == null); // null one means admin does not set status & dto will
                                   // handle this to set it as NOT_SET
    }

}