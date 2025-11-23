package com.saiteja.flightbookingwebfluxmongo.dto.flight;

import com.saiteja.flightbookingwebfluxmongo.model.enums.Airline;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightCreateRequest {

    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotNull(message = "Airline is required")
    private Airline airline;

    @NotBlank(message = "Origin airport is required")
    private String originAirport;

    @NotBlank(message = "Destination airport is required")
    private String destinationAirport;

    @Min(1)
    @Max(1000)
    private Integer seatCapacity;
}
