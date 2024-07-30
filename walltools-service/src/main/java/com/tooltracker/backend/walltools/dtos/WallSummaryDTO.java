package com.tooltracker.backend.walltools.dtos;

import java.util.List;

import lombok.Data;

@Data
public class WallSummaryDTO {

    // Tool Summary
    private Long availableTools;
    private Long takenTools;
    private Long brokenTools;

    // Movement Summary
    private Long currentInUseMovements;
    private Long movementsForLastWeek;
    private Long allMovementsOnWall;

    // Users Summary
    private List<String> currentUsersWithToolsInUse;
    private List<String> usersTheyHaveUsingWall;
    private List<String> blockedUsers;

}
