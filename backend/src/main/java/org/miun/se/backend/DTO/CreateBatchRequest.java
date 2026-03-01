package org.miun.se.backend.DTO;

import java.util.List;

public record CreateBatchRequest(
        String batchType,
        List<CreateBatchItemRequest> items
) {}