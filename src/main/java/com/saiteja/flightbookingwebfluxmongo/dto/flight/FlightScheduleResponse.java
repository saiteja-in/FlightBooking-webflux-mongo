package com.saiteja.flightbookingwebfluxmongo.dto.flight;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class FlightScheduleResponse {

    private String scheduleId;
    private String flightNumber;
    private String airline;
    private String originAirport;
    private String destinationAirport;
    private LocalDate flightDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private BigDecimal fare;
    private Integer availableSeats;
}
