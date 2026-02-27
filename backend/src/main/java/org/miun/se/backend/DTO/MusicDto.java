package org.miun.se.backend.DTO;

import java.time.LocalDate;

public record MusicDto (

        String title,
        String description,
        LocalDate date,
        String imgPath

) {}
