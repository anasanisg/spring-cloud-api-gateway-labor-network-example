package com.tooltracker.backend.walltools.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.tooltracker.backend.walltools.entities.Movement;
import com.tooltracker.backend.walltools.enums.MovementStatus;

import jakarta.persistence.LockModeType;

public interface MovementRepo extends JpaRepository<Movement, Long> {

        Optional<Movement> findByUserIdAndMovementStatus(Long userId, MovementStatus status);

        List<Movement> findAllByUserIdAndMovementStatus(Long userId, MovementStatus status);

        List<Movement> findAllByMovementStatus(MovementStatus status);

        @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
        @Query("SELECT mov FROM Movement mov WHERE mov.movementStatus = 'IN_PROCESSING'")
        List<Movement> findMovementsInProcessing();

        List<Movement> findByMovementStatusAndCreatedAtBetween(MovementStatus status, LocalDateTime fromDate,
                        LocalDateTime toDate);

        List<Movement> findByMovementStatusAndReturnDateBetween(MovementStatus status, LocalDateTime fromDate,
                        LocalDateTime toDate);

}
