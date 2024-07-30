package com.tooltracker.backend.walltools.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tooltracker.backend.walltools.dtos.ToolDTO;
import com.tooltracker.backend.walltools.dtos.ToolDetailsDTO;
import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.entities.ToolDetails;
import com.tooltracker.backend.walltools.enums.ToolStatus;
import com.tooltracker.backend.walltools.exceptions.NotFoundToolIdException;
import com.tooltracker.backend.walltools.exceptions.ToolNameIsExistedException;
import com.tooltracker.backend.walltools.repositories.ToolRepo;
import com.tooltracker.backend.walltools.services.SharedDataService;
import com.tooltracker.backend.walltools.services.SharedDataServiceImpl;
import com.tooltracker.backend.walltools.services.ToolServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ToolServiceTest {

    private ToolServiceImpl toolService;
    private ToolRepo toolRepo;
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        modelMapper = mock(ModelMapper.class);
        toolRepo = mock(ToolRepo.class);
        toolService = new ToolServiceImpl(toolRepo, modelMapper);
    }

    @Test
    @DisplayName("Test creating a new tool when tool name is unique")
    public void createToolTest() {
        // create a Tool
        ToolDTO toolDTO = createMockToolDTO();
        Tool mockTool = createMockTool(toolDTO);

        when(toolRepo.save(any())).thenReturn(mockTool);
        when(toolRepo.findByToolName(toolDTO.getToolName())).thenReturn(Optional.empty());
        when(modelMapper.map(toolDTO, Tool.class)).thenReturn(mockTool);
        Tool result = toolService.createTool(toolDTO);
        assertNotNull(result);
        assertEquals(mockTool, result);
        verify(toolRepo, times(1)).save(mockTool);
        verify(toolRepo, times(1)).findByToolName(toolDTO.getToolName());
        verify(modelMapper, times(1)).map(toolDTO, Tool.class);
    }

    @Test
    @DisplayName("Test creating a new tool when tool name already exists")
    public void createTool_ToolNameExists() {
        // createTool_ToolNameExists
        ToolDTO toolDTO = createMockToolDTO();
        when(toolRepo.findByToolName(toolDTO.getToolName())).thenReturn(Optional.of(createMockTool(toolDTO)));
        assertThrows(ToolNameIsExistedException.class, () -> toolService.createTool(toolDTO));
        verify(toolRepo, never()).save(any());
        verify(modelMapper, never()).map(any(), any());

    }

    @Test
    @DisplayName("Update Tool Details - Successful Update")
    public void updateToolDetailsTest() {

        Long toolId = 78L;
        ToolDetailsDTO toolDetailsDTO = createMockToolDetailsDTO();
        Tool existingTool = createMockToolWithDetails(toolId, "Multimeter");

        when(toolRepo.findById(toolId)).thenReturn(java.util.Optional.of(existingTool));
        when(toolRepo.save(any(Tool.class))).thenReturn(existingTool);
        Tool result = toolService.updateToolDetails(toolId, toolDetailsDTO);
        assertNotNull(result);
        assertNotNull(result.getToolDetails());
        assertEquals(toolDetailsDTO.getDescription(), result.getToolDetails().getDescription());
        assertEquals(toolDetailsDTO.getHeight(), result.getToolDetails().getHeight());
        assertEquals(toolDetailsDTO.getWidth(), result.getToolDetails().getWidth());
        assertEquals(toolDetailsDTO.getWeight(), result.getToolDetails().getWeight());
        assertEquals(toolDetailsDTO.getImg(), result.getToolDetails().getImg());
        verify(toolRepo, times(1)).findById(toolId);
        verify(toolRepo, times(1)).save(existingTool);
    }

    // ToolDTODetails
    private ToolDetailsDTO createMockToolDetailsDTO() {
        ToolDetailsDTO toolDetailsDTO = new ToolDetailsDTO();
        toolDetailsDTO.setDescription("New Multimeter Description");
        toolDetailsDTO.setHeight(2.5);
        toolDetailsDTO.setWidth(1.0);
        toolDetailsDTO.setWeight(1.5);
        toolDetailsDTO.setImg("Multimeter.jpg");
        return toolDetailsDTO;
    }

    @Test
    @DisplayName("Update Tool Details when Tool Not Found")
    public void updateToolDetails_ToolNotFoundTest() {

        Long toolId = 1L;
        ToolDetailsDTO toolDetailsDTO = createMockToolDetailsDTO();

        when(toolRepo.findById(toolId)).thenReturn(java.util.Optional.empty());
        assertThrows(NotFoundToolIdException.class, () -> toolService.updateToolDetails(toolId, toolDetailsDTO));
        verify(toolRepo, times(1)).findById(toolId);
        verify(toolRepo, never()).save(any(Tool.class));
    }

    @Test
    @DisplayName("Test for updateToolStatus")
    public void updateToolStatusTest() {

        Long toolId = 78L;
        Map<String, Object> newStatus = new HashMap<>();
        newStatus.put("status", "GOOD");

        Tool existingTool = new Tool();
        existingTool.setId(toolId);
        existingTool.setToolStatus(ToolStatus.BROKEN);

        when(toolRepo.findById(toolId)).thenReturn(Optional.of(existingTool));
        when(toolRepo.save(any(Tool.class))).thenReturn(existingTool);
        Tool result = toolService.updateToolStatus(toolId, newStatus);
        assertNotNull(result);
        assertEquals(ToolStatus.GOOD, result.getToolStatus());
        verify(toolRepo, times(1)).findById(toolId);
        verify(toolRepo, times(1)).save(existingTool);
    }

    @Test
    @DisplayName("Test for updateToolStatus with non-existing tool")
    public void updateToolStatus_ToolNotExistTest() {

        long toolId = 30L;
        Map<String, Object> newStatus = new HashMap<>();
        newStatus.put("status", "GOOD");

        when(toolRepo.findById(toolId)).thenReturn(Optional.empty());
        assertThrows(NotFoundToolIdException.class, () -> toolService.updateToolStatus(toolId, newStatus));
        verify(toolRepo, times(1)).findById(toolId);
        verify(toolRepo, never()).save(any(Tool.class));
    }

    @Test
    @DisplayName("getToolById: Return tool when tool with specified ID exists")
    public void getToolByIdTest() {

        Tool mockTool = createMockTool(79L, "Schwämmchen");
        when(toolRepo.findById(79L)).thenReturn(Optional.of(mockTool));
        Tool result = toolService.getToolById(79L);
        assertNotNull(result);
        assertEquals(mockTool, result);
    }

    @Test
    @DisplayName("getToolById: NotFoundToolIdException when tool with specified ID does not exist")
    public void getToolById_ToolNotExistsTest() {

        when(toolRepo.findById(51L)).thenReturn(Optional.empty());
        assertThrows(NotFoundToolIdException.class, () -> toolService.getToolById(51L));
    }

    @Test
    @DisplayName("Test getting all tools")
    public void getAllToolsTest() {
        // zero Tool inputs, returns an empty list when no tools exist
        when(toolRepo.findAll()).thenReturn(Collections.emptyList());
        List<Tool> result1 = toolService.getAllTools();
        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        // Returns a list of tools when tools exist
        List<Tool> mockTools1 = Arrays.asList(
                createMockTool(77L, "Cabelschere"),
                createMockTool(78L, "Multimeter"),
                createMockTool(79L, "Schwämmchen"));
        when(toolRepo.findAll()).thenReturn(mockTools1);
        List<Tool> result2 = toolService.getAllTools();
        assertNotNull(result2);
        assertEquals(mockTools1.size(), result2.size());
        assertEquals(mockTools1, result2);

    }

    @Test
    @DisplayName("Test für get all Tools by Status")
    public void getAllToolsByStatusTest()

    {
        Tool tool1 = new Tool();
        tool1.setId(77L);
        tool1.setToolName("Cabelschere");
        tool1.setToolStatus(ToolStatus.GOOD);

        Tool tool2 = new Tool();
        tool2.setId(78L);
        tool2.setToolName("Multimeter");
        tool2.setToolStatus(ToolStatus.GOOD);

        Tool tool3 = new Tool();
        tool3.setId(79L);
        tool3.setToolName("Schwämmchen");
        tool3.setToolStatus(ToolStatus.BROKEN);

        List<Tool> tools = Arrays.asList(tool1, tool2, tool3);

        when(toolRepo.findByToolStatus(ToolStatus.GOOD)).thenReturn(Arrays.asList(tool1, tool2));
        when(toolRepo.findByToolStatus(ToolStatus.BROKEN)).thenReturn(Arrays.asList(tool3));
        List<Tool> resultGood = toolService.getAllToolsByStatus(ToolStatus.GOOD);
        List<Tool> resultBroken = toolService.getAllToolsByStatus(ToolStatus.BROKEN);
        assertEquals(2, resultGood.size());
        assertEquals(1, resultBroken.size());
    }

    private ToolDTO createMockToolDTO() {
        ToolDTO toolDTO = new ToolDTO();
        toolDTO.setToolName("Multimeter");

        return toolDTO;
    }

    private Tool createMockTool(ToolDTO toolDTO) {
        Tool mockTool = new Tool();
        mockTool.setId(1L);
        mockTool.setToolName(toolDTO.getToolName());

        return mockTool;
    }

    private Tool createMockTool(long toolId) {
        Tool mockTool = new Tool();
        mockTool.setId(toolId);
        mockTool.setToolName("TestTool");
        return mockTool;
    }

    private Tool createMockTool(Long id, String toolName) {
        // Create and return a mock Tool object with the specified ID and toolName
        Tool mockTool = mock(Tool.class);
        when(mockTool.getId()).thenReturn(id);
        when(mockTool.getToolName()).thenReturn(toolName);
        return mockTool;
    }

    private Tool createMockToolWithDetails(Long toolId, String toolName) {
        Tool tool = new Tool();
        tool.setId(toolId);
        tool.setToolName(toolName);

        ToolDetails toolDetails = new ToolDetails();
        toolDetails.setId(78L); // Set a sample ID
        toolDetails.setDescription("New Multimeter Description");
        toolDetails.setHeight(2.5);
        toolDetails.setWidth(1.0);
        toolDetails.setWeight(1.5);
        toolDetails.setImg("Multimeter.jpg");

        tool.setToolDetails(toolDetails);
        return tool;
    }

}
