package com.tooltracker.backend.walltools.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tooltracker.backend.walltools.dtos.ResponseShape;
import com.tooltracker.backend.walltools.dtos.ToolDTO;
import com.tooltracker.backend.walltools.dtos.ToolDetailsDTO;
import com.tooltracker.backend.walltools.enums.ResponseStatus;
import com.tooltracker.backend.walltools.enums.ToolStatus;
import com.tooltracker.backend.walltools.services.SharedDataService;
import com.tooltracker.backend.walltools.services.ToolService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@OpenAPIDefinition(info = @Info(title = "Wand Tool", version = "v1.0", description = "My wallTools service documentation."))
@Tag(name = "ToolController")

@RestController
@RequestMapping("/${tooltracker.walltools.api.currentversion}/tool")
@AllArgsConstructor
public class ToolController {

        private SharedDataService sharedDataService;
        private ToolService toolService;
        private ModelMapper modelMapper;

        @Operation(summary = "Create Tools")
        @PostMapping
        public ResponseEntity<ResponseShape> createTool(@Valid @RequestBody ToolDTO tool) {
                // to Execute (Creation Tool logic) & Map Entity to DTO
                ToolDTO respone = modelMapper.map(toolService.createTool(tool), ToolDTO.class);
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, respone),
                                HttpStatus.CREATED);
        }

        @Operation(summary = "Update Tool Details")
        @PutMapping("/{id}")
        public ResponseEntity<ResponseShape> updateToolDetails(@PathVariable Long id,
                        @RequestBody ToolDetailsDTO toolDetails) {
                // to Execute Update Operation & Map Entity to DTO
                ToolDTO response = modelMapper.map(toolService.updateToolDetails(id, toolDetails), ToolDTO.class);

                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, response), HttpStatus.OK);
        }

        @Operation(summary = "delete Tool")
        @PutMapping("/{id}/status")
        public ResponseEntity<ResponseShape> updateToolStatus(@PathVariable Long id,
                        @RequestBody Map<String, Object> status) {
                // to Execute updateToolStatus & Map Entity to DTO
                ToolDTO response = modelMapper.map(toolService.updateToolStatus(id, status), ToolDTO.class);
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, response),
                                HttpStatus.OK);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseShape> getToolById(@PathVariable Long id) {
                // to Execute get tool & mapping entity to dto
                ToolDTO response = modelMapper.map(toolService.getToolById(id), ToolDTO.class);
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, response),
                                HttpStatus.OK);
        }

        @GetMapping("/all")
        public ResponseEntity<ResponseShape> getAllTools() {

                // to Execute getAllTools & Map List<Tool> to List of DTOS
                List<ToolDTO> response = toolService.getAllTools().stream()
                                .map(tool -> modelMapper.map(tool, ToolDTO.class))
                                .collect(Collectors.toList());

                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, response),
                                HttpStatus.OK);
        }

        @GetMapping("/by")
        public ResponseEntity<ResponseShape> findAllToolsByStatus(
                        @RequestParam(name = "status", required = true) ToolStatus status) {

                // to Execute getAllToolsByStatus & Map List<Tool> to List of DTOS
                List<ToolDTO> response = toolService.getAllToolsByStatus(status).stream()
                                .map(tool -> modelMapper.map(tool, ToolDTO.class))
                                .collect(Collectors.toList());

                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, response),
                                HttpStatus.OK);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseShape> deleteTool(@PathVariable Long id) {
                // to Execute Deletion Logic
                sharedDataService.deleteTool(id);
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, null), HttpStatus.OK);
        }

}
