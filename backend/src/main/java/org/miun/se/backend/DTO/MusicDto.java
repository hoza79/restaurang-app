package org.miun.se.backend.DTO;

import java.time.LocalDateTime;

public record MusicDto (
        Integer id,
        String title,
        String description,
        LocalDateTime date,
        String imgPath,
        Integer likes
) {}
