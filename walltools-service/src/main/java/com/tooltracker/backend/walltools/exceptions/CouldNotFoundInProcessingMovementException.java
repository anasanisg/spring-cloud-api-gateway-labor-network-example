package com.tooltracker.backend.walltools.exceptions;

public class CouldNotFoundInProcessingMovementException extends RuntimeException {

    public CouldNotFoundInProcessingMovementException() {
        super("Could not found in processing movement; maybe some one are taking tools without scanning");
    }

}
