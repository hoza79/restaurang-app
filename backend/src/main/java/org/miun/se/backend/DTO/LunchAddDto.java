package org.miun.se.backend.DTO;
import java.time.LocalDate;

public record LunchAddDto(
        String name,
        String description,
        Double price,
        LocalDate availableDate
) {}