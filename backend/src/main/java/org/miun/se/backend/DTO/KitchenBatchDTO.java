package org.miun.se.backend.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record KitchenBatchDTO(
        Integer batchId,
        String batchType,
        String batchStatus,
        LocalDateTime createdAt,
        Integer tableNumber,
        List<KitchenItemDTO> items
) {}