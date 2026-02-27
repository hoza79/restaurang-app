package org.miun.se.backend.DTO;

import java.util.List;

public record MenuDto(
        List<MenuCarteItemDto> appetizers,
        List<MenuCarteItemDto> mainCourses,
        List<MenuCarteItemDto> desserts,
        List<MenuCarteItemDto> drinks
) {}