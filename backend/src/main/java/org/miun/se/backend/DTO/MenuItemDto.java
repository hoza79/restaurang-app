package org.miun.se.backend.DTO;

import java.time.LocalDate;

public record MenuItemDto (

    String name,
    Integer menuItemId,
    String description,
    Double price,
    Boolean available,
    LocalDate mealDay
) {}
