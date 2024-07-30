package com.tooltracker.backend.walltools.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tooltracker.backend.walltools.dtos.MovementDTO;
import com.tooltracker.backend.walltools.dtos.ToolDTO;
import com.tooltracker.backend.walltools.entities.Movement;
import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.enums.MovementStatus;
import com.tooltracker.backend.walltools.exceptions.CouldNotFoundInProcessingMovementException;
import com.tooltracker.backend.walltools.exceptions.ExceededInProcessingMovementLimitException;
import com.tooltracker.backend.walltools.exceptions.NotFoundMovementIdException;
import com.tooltracker.backend.walltools.exceptions.SensorSendInvalidIDsException;
import com.tooltracker.backend.walltools.exceptions.OngoingInProcessMovementException;
import com.tooltracker.backend.walltools.exceptions.ToolIsNotRecordedInMovementException;
import com.tooltracker.backend.walltools.repositories.MovementRepo;
import com.tooltracker.backend.walltools.utils.CollectionUtils;
import com.tooltracker.backend.walltools.utils.EnumsConverter;

import lombok.AllArgsConstructor;

/*
 * Movement Process 
 * [1] Use scan his card 
 * [2] the card is verified by user service
 * [3] api-gateway redirect request to registerMovement
 * [4] Business-Logic will check if there is any movement with type (In_Processing) if yes 
 * the movement will be rejected if no then continue to step 5
 * [5] adding new movement with type (In_Processing)
 * [6] in real enviroment after 20s the sensor will send request to takeTools functions with the list of taken tools
 * @@@[7] busniss logic will get the in_processing movement and update takenTools list && update the status to be in_use
 * [8] when user return all tools then the movement status will be returned and the movement completed
 * 
 * NOTE 1: the movement table can have one movement with type in_processing at the same time; this will be handled in business logic 
 * NOTE 2: the user can have one movement with type in_use; so when user decide to return some tools or taken another tools
 * all of this will be recorded to the in_use movement that user already have.
 * NOTE 3: On every return_tool the busniss will check if user has return all tools or not because every tool is related with movement of user
 * so when user return all tools then the movement status will be returned and movement completed
 * NOTE 4: the user can have muliple movements with type Returned ---> for archive; this will help admin to track every single movement in the future
 */

@Service
@AllArgsConstructor
public class MovementServiceImpl implements MovementService {

    private MovementRepo movementRepo;

    private ToolService toolService;

    @Override // THE MOST SENSITIVE ENDPOINT: ITS PROTECTED BY REDIS SERVER
    @Transactional
    public void registerMovement(MovementDTO movement) {

        /*
         * Logic - 1:
         * Check if there is any IN_PROCESSING movement
         */

        List<Movement> ret = getMovementsByStatus(MovementStatus.IN_PROCESSING);

        if (ret.size() > 0) {

            if (ret.size() > 1) {
                /*
                 * Logging the current situation to admin; this should not occur at any
                 * situation;
                 */

                throw new ExceededInProcessingMovementLimitException();
            }

            throw new OngoingInProcessMovementException(movement.getUserId().toString(),
                    ret.get(0).getId().toString());
        }

        /*
         * Logic - 2
         * Check if user has any ongoing (in_use) movement
         * if yes: then update the movement status to be in_processing
         * if no: then create new Movment
         */

        if (hasUserAnyInUseMovement(movement.getUserId())) {

            /*
             * Getting InuSE movement
             * To prevent any other user from scanning again;
             * because the user are taking some extra tools
             * again
             */
            Movement inUseMov = getUserMovementsByStatus(movement.getUserId(), MovementStatus.IN_USE).get(0);
            inUseMov.setMovementStatus(MovementStatus.IN_PROCESSING);
            movementRepo.save(inUseMov);

        } else {

            /*
             * Register new movement with status IN_PROCESSING
             */
            Movement toSv = new Movement();
            toSv.setUserId(movement.getUserId());
            toSv.setUserName(movement.getUserName());
            toSv.setTakenTools(new ArrayList<>());
            toSv.setMovementStatus(MovementStatus.IN_PROCESSING);

            movementRepo.save(toSv);
        }

    }

    @Override
    @Transactional
    public void takeListOfTools(MovementDTO movementDTO) {

        /*
         * Logic 1
         * Getting All MovementWith Status InProcessing which is always 1
         * the movement table can have 1 movement with status in_processing at the same
         * time
         */

        List<Movement> inProcessingMovs = movementRepo.findMovementsInProcessing();

        /*
         * This will be thrown if there are no in_Processing Mov
         * which means the user not scan his card or try to steal something
         */
        if (inProcessingMovs == null || inProcessingMovs.isEmpty())
            throw new CouldNotFoundInProcessingMovementException();

        /*
         * If this is thrown --> This could be a signal for DDOS ATTACK
         * This Faliour should not be there at any situation
         * Redis-server is responsible to protect scan endpoint
         * if this error thrown this is Global Failour in system & system should be
         * shutdown immediately
         */
        if (inProcessingMovs.size() > 1)
            throw new ExceededInProcessingMovementLimitException();

        Movement inProcessingMov = inProcessingMovs.get(0);

        /*
         * Logic - 2
         * Extracting tools from sendet request
         * & check if the tools are already registered in tool table
         * if no: Sensor may send incorrect Data so throw exception and logging the
         * error to admin
         * if yes: then continue this is normal behaviour
         */

        // Extract ToolIds the user has taken from DTO
        List<Long> extractedTakenToolsId = CollectionUtils.extractIds(movementDTO.getTakenTools(), ToolDTO::getId);

        // Return the Entities of taken tool from Database
        List<Tool> takenTools = toolService.getAllToolsByListOfIds(extractedTakenToolsId);

        /*
         * REMEMBER : Each sensor is associated with a specific ToolId, implying that in
         * a real-world scenario, a single user can utilize the same tool during a given
         * movement.
         */

        /*
         * Check If Tools is Already registered in any movement or not recorded in tool
         * table
         * [1] Yes there are: throw exception; In this scenario this is invalid request,
         * the
         * sensor may send invalid payload; so throw exception & logging exception for
         * admin
         * [2] No there are not : then continue to create Movement || update current
         * in_use movement
         */

        for (Tool tool : takenTools)
            if (tool.getMovements().size() != 0) // check if tool has movement
                throw new SensorSendInvalidIDsException(Collections.singletonList(tool.getId()));

        /*
         * logic - 3 :
         * Update Taken tool in the IN_PROCESSING Movement
         * Here we have 2 Scenarios
         * [1] User have take some tools
         * [2] User have take nothing
         */

        List<Tool> userTakenTools = inProcessingMov.getTakenTools();
        userTakenTools.addAll(takenTools);
        inProcessingMov.setTakenTools(userTakenTools);

        if (userTakenTools.size() == 0) // User has scan but he did not take any thing
        {
            inProcessingMov.setMovementStatus(MovementStatus.RETURNED);
            inProcessingMov.setReturnDate(LocalDateTime.now());
        } else // User talk some tools
            inProcessingMov.setMovementStatus(MovementStatus.IN_USE);

        // sv in db
        movementRepo.save(inProcessingMov);
    }

    @Override
    @Transactional
    public void returnTool(Long toolId) {

        // get tool entity the user want return it
        Tool toBeReturnedTool = toolService.getToolById(toolId);

        /*
         * Retrieve all movements associated with this tool. In a typical scenario,
         * there should only be one movement, as a tool can only be taken by a single
         * user.
         * Therefore, we can safely extract the movement using:
         * Movement mov = inUseTool.getMovements().get(0);
         *
         * However, to ensure robust functionality, we are using a loop.
         */

        List<Movement> movs = toBeReturnedTool.getMovements();

        /*
         * User Try to return tool that are not recording in any movement:
         * [1] Trhow Exception
         * [2] Recording the operation in the log to notify the administrator
         */
        if (movs.size() == 0)
            throw new ToolIsNotRecordedInMovementException(toolId.toString());

        /*
         * Logic [1]: Remove returned tool from movement
         */

        List<Tool> toBeUpdated = null;
        for (Movement mov : movs) {
            toBeUpdated = mov.getTakenTools();
            toBeUpdated.remove(toBeReturnedTool);
            movementRepo.save(mov);
        }

        /*
         * Logic [2]: Update status of movement to be RETURNED
         * if there are no taken tools then user has return all tools
         */

        Movement currentMovement = getMovementById(movs.get(0).getId());

        if (currentMovement.getTakenTools().size() == 0) {
            currentMovement.setMovementStatus(MovementStatus.RETURNED);
            currentMovement.setReturnDate(LocalDateTime.now());
            movementRepo.save(currentMovement);
        }

    }

    @Override
    public Movement forceUpdateMovementStatus(Long movementId, Map<String, Object> status) {
        Movement toSv = getMovementById(movementId);
        toSv.setMovementStatus(EnumsConverter.convertToMovementStatus(movementId.toString(), status.get("status")));
        return movementRepo.save(toSv);
    }

    @Override
    public List<Movement> getMovementsByStatus(MovementStatus movementStatus) {
        List<Movement> ret = movementRepo.findAllByMovementStatus(movementStatus);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        return ret;
    }

    @Override
    public Movement getMovementById(Long movementId) { // Should be updated
        Optional<Movement> mov = movementRepo.findById(movementId);

        if (!mov.isPresent()) // throw Exception if tool not found
            throw new NotFoundMovementIdException(movementId.toString());

        return mov.get();
    }

    /*
     * This func is used to Return List<Movement> depend on Two Factor
     * [1] Status = IN_PROCESS, IN_USE, RETURNED
     * [2] Date (fromDate to Date)
     * NOTE for IN_PROCESS & IN_USE movements it will return depend on
     * pickUpDate(creationAt)
     * NOTE for returned it will return depend on return date
     */
    @Override
    public List<Movement> getMovementsByStatusAndDate(MovementStatus status, LocalDate fromDate,
            LocalDate toDate) { // ---

        if (status == MovementStatus.IN_PROCESSING || status == MovementStatus.IN_USE)
            return movementRepo.findByMovementStatusAndCreatedAtBetween(status, fromDate.atStartOfDay(),
                    toDate.atTime(LocalTime.MAX));

        // Return with (Status == returned & compare by returnDate)
        return movementRepo.findByMovementStatusAndReturnDateBetween(status, fromDate.atStartOfDay(),
                toDate.atTime(LocalTime.MAX));
    }

    @Override
    public List<Movement> getUserMovementsByStatus(Long userId, MovementStatus status) {
        return movementRepo.findAllByUserIdAndMovementStatus(userId, status);
    }

    @Override // TODO : NOT WORK TO CHECK
    public List<Movement> getInUseMovementsForMoreThan(int hours) {

        LocalDateTime curretTime = LocalDateTime.now();
        List<Movement> movs = movementRepo.findAllByMovementStatus(MovementStatus.IN_USE);

        List<Movement> ret = new ArrayList<>();
        for (Movement movement : movs) {
            Duration diff = Duration.between(movement.getCreatedAt(), curretTime);

            if (diff.toHours() >= hours)
                ret.add(movement);
        }

        return ret;
    }

    @Override
    public Map<String, Object> getWallSummary() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getWallSummary'");
    }

    private boolean hasUserAnyInUseMovement(Long userId) {
        return movementRepo.findByUserIdAndMovementStatus(userId, MovementStatus.IN_USE).isPresent();
    }

    @Override
    public void forceDeleteToolFromTakenToolList(Long toolId) {

        /*
         * Getting the tool that would be deleted
         */

        Tool toBeReturnedTool = toolService.getToolById(toolId);

        /*
         * Removing the tool that admin want to delete from taken Tools
         */

        List<Tool> takenTools = toBeReturnedTool.getMovements().get(0).getTakenTools();
        takenTools.remove(toBeReturnedTool);

        /*
         * if user don't have any other movement then update status to be returned
         */
        if (takenTools.size() == 0)
            toBeReturnedTool.getMovements().get(0).setMovementStatus(MovementStatus.RETURNED);

        toBeReturnedTool.getMovements().get(0).setTakenTools(takenTools);
        movementRepo.save(toBeReturnedTool.getMovements().get(0));

    }

    /*
     * @Override
     * 
     * @Transactional // #### NOT TESTED NOT TESTED
     * public void returnListOfTools(List<ToolDTO> tools) {
     * 
     * List<Long> extractedIds = CollectionUtils.extractIds(tools, ToolDTO::getId);
     * 
     * // Getting Tools
     * List<Tool> returnedTools = toolService.getAllToolsByListOfIds(extractedIds);
     * 
     * List<Tool> toBeUpdated;
     * for (Tool tool : returnedTools) {
     * for (Movement mov : tool.getMovements()) {
     * toBeUpdated = mov.getTakenTools();
     * toBeUpdated.remove(tool);
     * movementRepo.save(mov);
     * }
     * }
     * 
     * }
     * 
     */
}
