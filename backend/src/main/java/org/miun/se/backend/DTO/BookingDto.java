package org.miun.se.backend.DTO;

import java.time.LocalDateTime;

public record BookingDto(
        Integer bookingId,
        String firstName,
        String lastName,
        String phoneNumber,
        Integer guestCount,
        LocalDateTime date,
        Integer tableId,
        Integer tableNumber

) { }
