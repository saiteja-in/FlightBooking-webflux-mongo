package com.saiteja.flightbookingwebfluxmongo.model;

import com.saiteja.flightbookingwebfluxmongo.model.enums.Airline;
import com.saiteja.flightbookingwebfluxmongo.model.enums.FlightStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flight_schedules")
public class FlightSchedule {

    @Id
    private String id;
    @NotBlank(message = "flight reference is required")
    private String flightId; //ref to flight

    @NotNull(message = "Flight date is required")
    @FutureOrPresent(message = "Flight date cannot be in the past")
    private LocalDate flightDate;

    @NotNull(message = "Departure time is required")
    private LocalTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalTime arrivalTime;

    @NotNull(message = "Fare is required")
    private BigDecimal fare; //using float or double for currency is not a good idea because 0.1+0.2 = 0.30000000000000004

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    @NotNull(message = "Available seats is required")
    @Min(value = 0, message = "Available seats cannot be negative")
    private Integer availableSeats;

    @NotNull(message = "Flight status is required")
    private FlightStatus status;

    private List<String> bookedSeats;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
