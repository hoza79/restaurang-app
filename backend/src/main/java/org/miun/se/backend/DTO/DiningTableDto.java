package org.miun.se.backend.DTO;

import org.miun.se.backend.model.enums.TableStatus;

public record DiningTableDto(
        Integer tableId,
        Integer tableNumber,
        Integer capacity,
        TableStatus tableStatus
) {}