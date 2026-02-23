package org.miun.se.backend.DTO;



public record MenuCarteItemDto (

        String name,
        Integer menuItemId,
        String description,
        Double price,
        Boolean available
) {}
