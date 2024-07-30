package com.tooltracker.backend.walltools.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorDTO {
    private String errorCode;
    private String errorMsg;
}
