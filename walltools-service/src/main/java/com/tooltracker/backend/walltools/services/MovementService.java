package com.tooltracker.backend.walltools.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.tooltracker.backend.walltools.dtos.MovementDTO;
import com.tooltracker.backend.walltools.entities.Movement;
import com.tooltracker.backend.walltools.enums.MovementStatus;

/*
 * Movement Process 
 * [1] Use scan his card 
 * [2] the card is verified by user service
 * [3] api-gateway redirect request to registerMovement
 * [4] Business-Logic will check if there is any movement with type (In_Processing) if yes 
 * the movement will be rejected if no then continue to step 5
 * [5] adding new movement with type (In_Processing)
 * [6] in real enviroment after 20s the sensor will send request to takeTools functions with the list of taken tools
 * [7] busniss logic will get the in_processing movement and update takenTools list && update the status to be in_use
 * [8] when user return all tools then the movement status will be returned and the movement completed
 * 
 * NOTE 1: the movement table can have one movement with type in_processing at the same time; this will be handled in business logic 
 * NOTE 2: the user can have one movement with type in_use; so when user decide to return some tools or taken another tools
 * all of this will be recorded to the in_use movement that user already have.
 * NOTE 3: On every return_tool the busniss will check if user has return all tools or not because every tool is related with movement of user
 * so when user return all tools then the movement status will be returned and movement completed
 * NOTE 4: the user can have muliple movements with type Returned ---> for archive; this will help admin to track every single movement in the future
 */

public interface MovementService {

    void registerMovement(MovementDTO movement); // [ROLE - USER - ADMIN]

    // --------------------------------------

    void takeListOfTools(MovementDTO movement); // [ROLE - Admin - USER]

    void returnTool(Long toolId); // [Role Admin - User]

    List<Movement> getMovementsByStatus(MovementStatus movementStatus); // [ROLE - ADMIN]

    Movement getMovementById(Long movementId); // [ROLE - ADMIN]

    List<Movement> getMovementsByStatusAndDate(MovementStatus status, LocalDate fromDate, LocalDate toDate); // [ROLE
                                                                                                             // ADMIN]

    List<Movement> getUserMovementsByStatus(Long userId, MovementStatus status);// [ROLE ADMIN - USER]: user can access
                                                                                // only the movements that related with
                                                                                // him

    /*
     * Used to get movements depend on Use Time
     * Example : Lets Say the admin want to see the movements that take for more
     * than 4
     * hours & still not returned
     */
    List<Movement> getInUseMovementsForMoreThan(int hours); // [ROLE - ADMIN]

    Map<String, Object> getWallSummary(); // [ROLE ADMIN]

    // ------ Force Functions [All Admin Role] ---------
    void forceDeleteToolFromTakenToolList(Long toolId); // [ROLE - ADMIN]

    Movement forceUpdateMovementStatus(Long movementId, Map<String, Object> status); // [ROLE - ADMIN]

}
