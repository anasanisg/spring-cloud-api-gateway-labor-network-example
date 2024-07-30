package com.tooltracker.backend.walltools.exceptions;

public class OngoingInProcessMovementException extends RuntimeException {

    public OngoingInProcessMovementException(String userId, String inProcessingMovementId) {
        super(String.format(
                "The user with id %s is trying to register a movement  while the movement with id %s is currently on processing.",
                userId, inProcessingMovementId));
    }

}
