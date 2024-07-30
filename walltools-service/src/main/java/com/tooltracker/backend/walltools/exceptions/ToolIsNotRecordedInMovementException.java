package com.tooltracker.backend.walltools.exceptions;

public class ToolIsNotRecordedInMovementException extends RuntimeException {

    public ToolIsNotRecordedInMovementException(String id) {
        super(String.format(
                "Users attempting to return a tool with id {%s} that is not recorded in any movement may encounter this error, which could be attributed to an invalid payload sent from the sensor.",
                id));
    }

}
