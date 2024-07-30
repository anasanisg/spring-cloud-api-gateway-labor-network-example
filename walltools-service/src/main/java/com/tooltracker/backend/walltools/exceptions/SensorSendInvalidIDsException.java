package com.tooltracker.backend.walltools.exceptions;

import java.util.List;

public class SensorSendInvalidIDsException extends RuntimeException {

    public SensorSendInvalidIDsException(List<Long> ids) {
        super(String.format(
                "The tools with IDs %s are not found in the database or the tools are already taken. This error might occur because sensors are sending IDs that do not exist in the tool table or already taken by user.",
                ids));
    }

}
