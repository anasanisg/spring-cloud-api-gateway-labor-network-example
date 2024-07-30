package com.tooltracker.backend.walltools.exceptions;

public class ToolNameIsExistedException extends RuntimeException {

    public ToolNameIsExistedException(String data) {
        super(String.format("You are trying to add tool with name %s , and this name is existed in the database",
                data));
    }

}
