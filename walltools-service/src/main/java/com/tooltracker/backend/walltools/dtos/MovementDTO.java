package com.tooltracker.backend.walltools.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tooltracker.backend.walltools.enums.MovementStatus;

import lombok.Data;

@Data
public class MovementDTO {

    private Long id;

    private Long userId;

    private String userName;

    private List<ToolDTO> takenTools;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM yyyy HH:mm:ss") // Setting Default Pattern for Time
    private LocalDateTime pickupDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM yyyy HH:mm:ss") // Setting Default Pattern for Time
    private LocalDateTime returnDate;

    private MovementStatus movementStatus;

}
