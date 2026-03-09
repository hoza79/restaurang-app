package org.miun.se.backend.DTO;

import org.miun.se.backend.model.enums.SwapStatus;

import java.time.LocalDateTime;

public record SwapRequestDto (
    Integer swapId,
    Integer senderId,
    Integer receiverId,
    Integer shiftId,
    SwapStatus swapStatus
) {}
