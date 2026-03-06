package org.miun.se.backend.DTO;

import java.time.LocalDateTime;

public record KitchenOrderDto(
        Integer orderId,
        Integer tableNumber,
        LocalDateTime createdAt,
        double totalPrice,
        String status
) {}