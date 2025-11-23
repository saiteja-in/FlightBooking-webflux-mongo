package com.saiteja.flightbookingwebfluxmongo.model;

import com.saiteja.flightbookingwebfluxmongo.model.Passenger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    private String id;

    @NotBlank(message = "PNR cannot be empty")
    private String pnr;

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @NotBlank(message = "Schedule ID is required")
    private String scheduleId;

    @NotEmpty(message = "Passenger list cannot be empty")
    private List<@Valid Passenger> passengers;

    @NotNull(message = "Issued time is required")
    private LocalDateTime issuedAt;
}
