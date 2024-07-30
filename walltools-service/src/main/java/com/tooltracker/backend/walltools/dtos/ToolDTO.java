package com.tooltracker.backend.walltools.dtos;

import com.tooltracker.backend.walltools.enums.ToolStatus;
import com.tooltracker.backend.walltools.validators.ValidToolStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ToolDTO {
    private Long id;

    @NotBlank // For validation to check if toolname not blank
    private String toolName;

    private ToolDetailsDTO toolDetails;

    @ValidToolStatus // Custom Validator For status @ this stage not needed actually but we built it
                     // to handle status in safe way in the future
    private ToolStatus toolStatus;
}
