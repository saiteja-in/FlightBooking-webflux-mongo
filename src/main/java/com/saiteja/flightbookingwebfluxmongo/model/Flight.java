package com.saiteja.flightbookingwebfluxmongo.model;

import com.saiteja.flightbookingwebfluxmongo.model.enums.Airline;
import com.saiteja.flightbookingwebfluxmongo.model.enums.FlightStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection="flights")
public class Flight {
    @Id
    private String id;

    @NotBlank(message = "flight number cannot be empty")
    @Pattern(
            regexp = "^[A-Z]{1,3}\\d{2,4}$",
            message = "invalid flight number format"
    )
    private String flightNumber;
    @NotNull(message = "airline can't be null")
    private Airline airline;

    @NotBlank(message = "origin airport is required")
    private String originAirport;
    @NotBlank(message = "destination airport is required")
    private String destinationAirport;

    @NotNull(message = "seat capacity is required")
    @Min(value=1, message="Seat capacity must be at least 1")
    @Max(value=1000 , message="Seat capacity cannot exceed 1000")
    private Integer seatCapacity;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
