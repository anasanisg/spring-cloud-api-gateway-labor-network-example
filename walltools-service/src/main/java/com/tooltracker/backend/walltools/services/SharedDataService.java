package com.tooltracker.backend.walltools.services;

/*
 * To inject functionality that can affect two tables at the same time and avoid circular dependencies injection between beans.
 * and to keep the Tool service is fully isolated from Movement.
 */
public interface SharedDataService {

    /*
     * Deletion Functionality
     * [1] Check if tool is registered in any IN_USE movement
     * [2] Delete the tool from takenTool List
     * [3] Delete Tool
     */
    void deleteTool(Long toolId); // [ROLE - ADMIN]: Admin only can delete tools from werkzeugwand

}
