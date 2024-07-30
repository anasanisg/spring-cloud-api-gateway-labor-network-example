package com.tooltracker.backend.walltools.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tooltracker.backend.walltools.entities.Tool;
import com.tooltracker.backend.walltools.enums.ToolStatus;

public interface ToolRepo extends CrudRepository<Tool, Long> {

    Optional<Tool> findByToolName(String toolName);

    List<Tool> findByToolStatus(ToolStatus toolStatus);

    List<Tool> findAllByIdIn(List<Long> toolIds);

}
