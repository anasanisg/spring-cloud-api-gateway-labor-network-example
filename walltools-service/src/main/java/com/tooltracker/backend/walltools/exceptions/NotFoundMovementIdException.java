package com.tooltracker.backend.walltools.exceptions;

public class NotFoundMovementIdException extends RuntimeException {

    public NotFoundMovementIdException(String data) {
        super(String.format("The movement with Id %s not found",
                data));
    }

}
