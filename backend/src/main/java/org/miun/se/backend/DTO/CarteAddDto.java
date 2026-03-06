package org.miun.se.backend.DTO;

public record CarteAddDto(
        String name,
        String description,
        Double price,
        Boolean options
) {}