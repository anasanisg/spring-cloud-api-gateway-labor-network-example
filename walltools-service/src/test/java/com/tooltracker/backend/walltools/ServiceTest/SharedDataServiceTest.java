package com.tooltracker.backend.walltools.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.exceptions.NotFoundToolIdException;
import com.tooltracker.backend.walltools.repositories.MovementRepo;
import com.tooltracker.backend.walltools.repositories.ToolRepo;
import com.tooltracker.backend.walltools.services.MovementService;
import com.tooltracker.backend.walltools.services.MovementServiceImpl;
import com.tooltracker.backend.walltools.services.SharedDataService;
import com.tooltracker.backend.walltools.services.SharedDataServiceImpl;
import com.tooltracker.backend.walltools.services.ToolServiceImpl;

public class SharedDataServiceTest {

    private SharedDataService sharedDataService;
    private ToolServiceImpl toolService;
    private MovementService movementService;

    private ToolRepo toolRepo;
    private MovementRepo movementRepo;

    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        toolRepo = mock(ToolRepo.class);
        movementRepo = mock(MovementRepo.class);
        modelMapper = mock(ModelMapper.class);

        toolService = new ToolServiceImpl(toolRepo, modelMapper);
        movementService = new MovementServiceImpl(movementRepo, toolService);
        sharedDataService = new SharedDataServiceImpl(movementService, toolRepo);

    }

    @Test
    @DisplayName("Test deleting an existing tool")
    public void deleteToolTest() {
        // delete one Tool
        long toolId = 77L;
        Tool mockTool = createMockTool(toolId);
        mockTool.setMovements(new ArrayList<>());

        when(toolRepo.findById(toolId)).thenReturn(Optional.of(mockTool));
        doNothing().when(toolRepo).delete(mockTool);
        sharedDataService.deleteTool(toolId);
        verify(toolRepo, times(1)).findById(toolId);
        verify(toolRepo, times(1)).delete(mockTool);
    }

    @Test
    @DisplayName("Test deleting a non-existing tool throws NotFoundToolIdException")
    public void deleteTool_NotFoundToolIdExceptionTest() {
        long nonExistingToolId = 80L;

        when(toolRepo.findById(nonExistingToolId)).thenReturn(Optional.empty());
        assertThrows(NotFoundToolIdException.class, () -> sharedDataService.deleteTool(nonExistingToolId));
        verify(toolRepo, times(1)).findById(nonExistingToolId);
        verify(toolRepo, never()).delete(any());
    }

    private Tool createMockTool(long toolId) {
        Tool mockTool = new Tool();
        mockTool.setId(toolId);
        mockTool.setToolName("TestTool");
        return mockTool;
    }

}
