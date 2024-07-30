package com.tooltracker.backend.walltools.exceptions;

public class ExceededInProcessingMovementLimitException extends RuntimeException {

    public ExceededInProcessingMovementLimitException() {
        super("The system has too many in processing movement");
    }

}
