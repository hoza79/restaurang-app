package org.miun.se.backend.DTO;

import org.miun.se.backend.model.enums.EmployeeRole;

import java.time.LocalDateTime;
import java.util.List;

public record EmployeeDto (
    Integer employeeId,
    String firstName,
    String lastName,
    EmployeeRole role,
    String phoneNumber,
    String emailAddress,
    List<ShiftDto> shifts,
    List<KitchenOrderDto> orders
) {}
