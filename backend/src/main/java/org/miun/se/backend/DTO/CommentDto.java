package org.miun.se.backend.DTO;


import java.time.LocalDateTime;

public record CommentDto(
        Integer commentId,
        String name,
        String message,
        Integer likes,
        LocalDateTime createdAt
) {}
