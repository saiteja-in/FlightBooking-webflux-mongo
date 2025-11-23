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
    private String flightId; //ref to flight

    private LocalDate flightDate;

    private LocalTime departureTime;

    private LocalTime arrivalTime;

    private BigDecimal fare; //using float or double for currency is not a good idea because 0.1+0.2 = 0.30000000000000004

    private Integer totalSeats;

    private Integer availableSeats;

    private FlightStatus status;

    private List<String> bookedSeats;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
