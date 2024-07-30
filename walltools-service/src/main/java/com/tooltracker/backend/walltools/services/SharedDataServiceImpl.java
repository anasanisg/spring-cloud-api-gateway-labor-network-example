package com.tooltracker.backend.walltools.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.exceptions.NotFoundToolIdException;
import com.tooltracker.backend.walltools.repositories.ToolRepo;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SharedDataServiceImpl implements SharedDataService {

    // private ToolService toolService;
    private MovementService movementService;
    private ToolRepo toolRepo;

    @Override
    @Transactional
    public void deleteTool(Long toolId) {
        ;
        Optional<Tool> delTool = toolRepo.findById(toolId);

        if (!delTool.isPresent()) // Check toolId in DB first
            throw new NotFoundToolIdException(toolId.toString());

        /*
         * Check if tool is registered in any IN_USE movement if ja then delete the tool
         * from the list inside movement
         */
        if (delTool.get().getMovements().size() != 0) {
            movementService.forceDeleteToolFromTakenToolList(toolId);
        }

        toolRepo.delete(delTool.get());
    }

}
