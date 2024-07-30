package com.tooltracker.backend.walltools.services;

import java.util.List;
import java.util.Map;

import com.tooltracker.backend.walltools.dtos.ToolDTO;
import com.tooltracker.backend.walltools.dtos.ToolDetailsDTO;
import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.enums.ToolStatus;

public interface ToolService {

    Tool createTool(ToolDTO tool); // [ROLE - ADMIN]: Admin only can add tools to werkzeugwand

    Tool updateToolDetails(Long toolId, ToolDetailsDTO toolDetails); // [ROLE - ADMIN]: Update Tool Details

    Tool updateToolStatus(Long toolId, Map<String, Object> newStatus); // [ROLE - ADMIN & User hav paritally acess to
                                                                       // set if tool is broken

    Tool getToolById(Long toolId); // [ROLE - ADMIN || USER] both can get tools & see tool details

    List<Tool> getAllTools(); // [ROLE - ADMIN || USER]

    List<Tool> getAllToolsByStatus(ToolStatus status); // [ROLE - ADMIN || USE ]

    List<Tool> getAllToolsByListOfIds(List<Long> ids);// ROLE ADMIN

}
