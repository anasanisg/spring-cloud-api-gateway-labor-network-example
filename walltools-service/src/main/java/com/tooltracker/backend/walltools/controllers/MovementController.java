package com.tooltracker.backend.walltools.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tooltracker.backend.walltools.dtos.MovementDTO;
import com.tooltracker.backend.walltools.dtos.ResponseShape;
import com.tooltracker.backend.walltools.enums.MovementStatus;
import com.tooltracker.backend.walltools.enums.ResponseStatus;
import com.tooltracker.backend.walltools.services.MovementService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/${tooltracker.walltools.api.currentversion}/movement")
@AllArgsConstructor
public class MovementController {

        private MovementService movementService;
        private ModelMapper modelMapper;

        /*
         * This is the most sensitive Endpoint point and it's protected by redis service
         * Can handle 1 RPS
         */
        @PostMapping("/scan")
        public ResponseEntity<ResponseShape> registerMovement(@RequestBody MovementDTO movement) {
                // to Execute (register new movement logic) & Map Entity to DTO
                movementService.registerMovement(movement);
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, null),
                                HttpStatus.CREATED);
        }

        @PostMapping
        public ResponseEntity<ResponseShape> createTool(@RequestBody MovementDTO movement) {
                // to Execute (Create new movement logic) & Map Entity to DTO
                movementService.takeListOfTools(movement);
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, null),
                                HttpStatus.CREATED);
        }

        @PatchMapping("/tool/{id}")
        public ResponseEntity<ResponseShape> returnTool(@PathVariable Long id) {
                // to Execute retrun tool Logic
                movementService.returnTool(id);
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, null), HttpStatus.OK);
        }

        @PatchMapping("/{id}/status")
        public ResponseEntity<ResponseShape> updateMovementStatus(@PathVariable Long id,
                        @RequestBody Map<String, Object> status) {
                // to Execute update Movement Stayus & Map Entity to DTO
                // to Execute updateToolStatus & Map Entity to DTO

                // movementService.forceUpdateMovementStatus(id, null)
                return new ResponseEntity<>(new ResponseShape(ResponseStatus.SUCCESS, null),
                                HttpStatus.OK);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseShape> getMovementById(@PathVariable Long id) {
                // to Execute get movement & mapping entity to dto
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.SUCCESS,
                                                modelMapper.map(movementService.getMovementById(id),
                                                                MovementDTO.class)),
                                HttpStatus.OK);
        }

        /*
         * This End-point is used to return List<Movements> by Status & between two date
         * if Status == IN_PROSESSING || IN_USE it will compare depend on pickupDate
         * between (fromDate,toDate )
         * if Status == RETURNED its will return depend on return date between (fromDate
         * & toDate)
         */

        @GetMapping("/by")
        public ResponseEntity<ResponseShape> getAllMovementsBy(
                        @RequestParam(name = "status", required = true) MovementStatus status,
                        @RequestParam(name = "fromDate", required = true) LocalDate fromDate,
                        @RequestParam(name = "toDate", required = true) LocalDate toDate) {

                // to Execute getAllMovementsBy & Map List<Movment> to List of DTOS
                List<MovementDTO> response = movementService.getMovementsByStatusAndDate(status, fromDate, toDate)
                                .stream()
                                .map(mov -> modelMapper.map(mov, MovementDTO.class))
                                .collect(Collectors.toList());

                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.SUCCESS,
                                                response),
                                HttpStatus.OK);
        }

        /*
         * This Endpoint used to return List<Movements> with status in_use that have
         * been used for more than specific hours
         * EXAMPLE: Give me all movements that are currently running for more than 2
         * hours & the user not return the tools yet
         */

        @GetMapping("/used-for-more-than")
        public ResponseEntity<ResponseShape> getAllInUseMovementsForMoreThan(
                        @RequestParam(name = "hours", required = true) int hours) {
                // to Execute getAllMovementsBy & Map List<Movment> to List of DTOS
                List<MovementDTO> response = movementService.getInUseMovementsForMoreThan(hours)
                                .stream()
                                .map(mov -> modelMapper.map(mov, MovementDTO.class))
                                .collect(Collectors.toList());

                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.SUCCESS,
                                                response),
                                HttpStatus.OK);
        }

}
