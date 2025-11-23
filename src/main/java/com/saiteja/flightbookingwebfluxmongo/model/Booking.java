package com.saiteja.flightbookingwebfluxmongo.model;

import com.saiteja.flightbookingwebfluxmongo.model.enums.BookingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;
    @NotBlank(message = "PNR cannot be empty")
    private String pnr;

    @NotEmpty(message = "At least one schedule must be selected")
    private List<@NotBlank String> scheduleIds;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    @NotEmpty(message = "At least one passenger is required")
    private List<@Valid Passenger> passengers;

    @NotNull(message = "Booking status is required")
    private BookingStatus status;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
