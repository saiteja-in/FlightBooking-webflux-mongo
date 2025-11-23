package com.saiteja.flightbookingwebfluxmongo.dto.flight;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FlightScheduleCreateRequest {

    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotNull
    @FutureOrPresent(message = "Flight date must be today or future")
    private LocalDate flightDate;

    @NotNull
    private LocalTime departureTime;

    @NotNull
    private LocalTime arrivalTime;

    @NotNull
    @DecimalMin(value = "1.00", message = "Fare must be at least 1.00")
    private BigDecimal fare;
}
