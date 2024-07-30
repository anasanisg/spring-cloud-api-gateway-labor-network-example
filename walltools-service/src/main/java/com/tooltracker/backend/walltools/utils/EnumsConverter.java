package com.tooltracker.backend.walltools.utils;

import com.tooltracker.backend.walltools.enums.MovementStatus;
import com.tooltracker.backend.walltools.enums.ToolStatus;

public class EnumsConverter {

    // Used to convert (Obj- status) to readable enum status
    public static ToolStatus convertToToolStatus(String toolId, Object status) {

        if (status == null)
            throw new IllegalArgumentException(
                    String.format("You are trying to update tool with id %s with null status", toolId));

        switch (status.toString()) {
            case "GOOD":
                return ToolStatus.GOOD;
            case "0":
                return ToolStatus.GOOD;
            case "TO_BE_REPLACED":
                return ToolStatus.TO_BE_REPLACED;
            case "1":
                return ToolStatus.TO_BE_REPLACED;
            case "BROKEN":
                return ToolStatus.BROKEN;
            case "2":
                return ToolStatus.BROKEN;
            case "NOT_SET":
                return ToolStatus.NOT_SET;
            case "3":
                return ToolStatus.NOT_SET;
            default:
                throw new IllegalArgumentException(
                        String.format("You are trying to update tool with id %s with unvalid status status", toolId));
        }

    }

    // Used to convert (Obj- status) to readable enum status
    public static MovementStatus convertToMovementStatus(String movementId, Object status) {

        if (status == null)
            throw new IllegalArgumentException(
                    String.format("You are trying to update movement with id %s with null status", movementId));

        switch (status.toString()) {
            case "IN_PROCESSING":
                return MovementStatus.IN_PROCESSING;
            case "0":
                return MovementStatus.IN_PROCESSING;
            case "IN_USE":
                return MovementStatus.IN_USE;
            case "1":
                return MovementStatus.IN_USE;
            case "RETURNED":
                return MovementStatus.RETURNED;
            case "2":
                return MovementStatus.RETURNED;
            default:
                throw new IllegalArgumentException(
                        String.format("You are trying to update movement with id %s with unvalid status status",
                                movementId));
        }

    }

}
