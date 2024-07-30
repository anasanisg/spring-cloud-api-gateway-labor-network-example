package com.tooltracker.backend.walltools.dtos;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tooltracker.backend.walltools.enums.ResponseStatus;

import lombok.Setter;

@Setter
public class ResponseShape {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM yyyy HH:mm:ss") // Setting Default Pattern for Time
    private LocalDateTime timestamp;
    private ResponseStatus state;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Exclue Response if its null
    private Object response;

    public ResponseShape() {
        this.timestamp = LocalDateTime.now();
    }

    public ResponseShape(ResponseStatus state) {
        this.timestamp = LocalDateTime.now();
        this.state = state;
    }

    public ResponseShape(ResponseStatus state, Object obj) {
        this.timestamp = LocalDateTime.now();
        this.state = state;
        this.response = obj;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public ResponseStatus getState() {
        return state;
    }

    public Object getResponse() {
        return response;
    }

}
