package org.miun.se.backend.DTO;


import java.time.LocalDateTime;

public record MusicAddDto (

        String title,
        String description,
        LocalDateTime date,
        String imgPath
){}

