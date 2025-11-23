package com.saiteja.flightbookingwebfluxmongo.dto.flight;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FlightSearchRequest {

    @NotBlank
    private String originAirport;

    @NotBlank
    private String destinationAirport;

    @NotNull
    private LocalDate flightDate;
}
