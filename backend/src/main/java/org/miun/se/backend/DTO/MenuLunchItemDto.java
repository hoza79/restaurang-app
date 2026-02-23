package org.miun.se.backend.DTO;

import java.time.LocalDate;

public record MenuLunchItemDto (

    String name,
    Integer menuItemId,
    String description,
    Double price,
    Boolean available,
    LocalDate mealDay
) {}
