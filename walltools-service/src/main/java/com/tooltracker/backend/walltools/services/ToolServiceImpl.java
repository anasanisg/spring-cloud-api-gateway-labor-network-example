package com.tooltracker.backend.walltools.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.tooltracker.backend.walltools.dtos.ToolDTO;
import com.tooltracker.backend.walltools.dtos.ToolDetailsDTO;
import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.entities.ToolDetails;
import com.tooltracker.backend.walltools.enums.ToolStatus;
import com.tooltracker.backend.walltools.exceptions.NotFoundToolIdException;
import com.tooltracker.backend.walltools.exceptions.SensorSendInvalidIDsException;
import com.tooltracker.backend.walltools.exceptions.ToolNameIsExistedException;
import com.tooltracker.backend.walltools.repositories.ToolRepo;
import com.tooltracker.backend.walltools.utils.CollectionUtils;
import com.tooltracker.backend.walltools.utils.EnumsConverter;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ToolServiceImpl implements ToolService {

    private ToolRepo toolRepo;
    private ModelMapper modelMapper;

    @Override
    public Tool createTool(ToolDTO tool) {

        if (isToolNameExisted(tool.getToolName())) // Tool name is unique as the id tool
            throw new ToolNameIsExistedException(tool.getToolName());

        return toolRepo.save(modelMapper.map(tool, Tool.class));
    }

    @Override
    public Tool updateToolDetails(Long toolId, ToolDetailsDTO toolDetails) {

        // Get tool after checking if it's existed
        Tool toSv = getToolById(toolId);

        /*
         * Note 1 : JPA will notify every new Instance of ToolDetailEntity as a new Row
         * in
         * Database & this will cause recursion problem & to solve recusrion problem
         * we are getting the exact object & refill it with the ToolDetailsDTO
         * Note 2 : modelMapper is set before in configuration to skip every null value,
         * so the null values will not being mapped
         * This will improve the updating technique & reduce code (by removing to many
         * set statment & if !null statements)
         * In general modelMapper is configured when the mapping Relation is <SOURCE =
         * ToolDetailsDTO , Destination= ToolDetailEntity> it will
         * skip every null to avoid & add nulls to DB and & Enhance update method so
         * every value that equal null in DTO means its not updated
         * check configutation in /src/..../modelmapperconfig
         */

        ToolDetails toBeUpdated = toSv.getToolDetails(); // Getting ToolDetailsEntity
        modelMapper.map(toolDetails, toBeUpdated); // mapping DTO to Entity

        return toolRepo.save(toSv);
    }

    @Override
    public Tool updateToolStatus(Long toolId, Map<String, Object> newStatus) {

        Tool toBeUpdated = getToolById(toolId); // Get tool if existed

        // This Will convert obj to ToolStatus && Check if its null && Check if value
        // valid
        ToolStatus toSvStatus = EnumsConverter.convertToToolStatus(toolId.toString(), newStatus.get("status"));
        toBeUpdated.setToolStatus(toSvStatus);

        return toolRepo.save(toBeUpdated);
    }

    @Override
    public Tool getToolById(Long toolId) {

        Optional<Tool> tool = toolRepo.findById(toolId);

        if (!tool.isPresent()) // throw Exception if tool not found
            throw new NotFoundToolIdException(toolId.toString());

        return tool.get();
    }

    @Override
    public List<Tool> getAllTools() {
        return (List<Tool>) toolRepo.findAll();
    }

    @Override
    public List<Tool> getAllToolsByStatus(ToolStatus status) {
        return (List<Tool>) toolRepo.findByToolStatus(status);
    }

    private Boolean isToolNameExisted(String toolName) {
        return toolRepo.findByToolName(toolName).isPresent();
    }

    @Override
    public List<Tool> getAllToolsByListOfIds(List<Long> ids) {

        List<Tool> ret = toolRepo.findAllByIdIn(ids);

        if (ids.size() != ret.size()) {

            /*
             * Extract Ids from returned list then compare the sendet ids with the existing
             * one in the database
             */
            List<Long> notExistedIds = CollectionUtils.getNotExistingElements(ids,
                    CollectionUtils.extractIds(ret, Tool::getId));

            /*
             * When user scan to take some tools, the sensors will send a request with
             * a list of IDs. However, if one or more IDs do not already exist in the tools
             * table, we observe that the sensor may be sending incorrect data. In such
             * cases, the backend will throw an exception and prevent the operation.
             */
            throw new SensorSendInvalidIDsException(notExistedIds);
        }

        return toolRepo.findAllByIdIn(ids);
    }

}
