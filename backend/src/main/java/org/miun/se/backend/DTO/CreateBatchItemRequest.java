package org.miun.se.backend.DTO;

public record CreateBatchItemRequest(
        Integer menuItemId,
        Integer quantity,
        String notes
) {}