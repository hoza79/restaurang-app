package org.miun.se.backend.DTO;

import org.miun.se.backend.model.enums.ShiftStatus;

import java.time.LocalDateTime;

public record ShiftDto (
    Integer shiftId,
    Integer employeeId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    ShiftStatus shiftStatus
) {}
