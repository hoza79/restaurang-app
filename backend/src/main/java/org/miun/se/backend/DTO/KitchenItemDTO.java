package org.miun.se.backend.DTO;

public record KitchenItemDTO(
        String name,
        Integer quantity,
        String notes
) {}