package com.saiteja.flightbookingwebfluxmongo.dto.booking;

import com.saiteja.flightbookingwebfluxmongo.dto.passenger.PassengerRequest;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookingCreateRequest {

    @NotBlank @Email
    private String contactEmail;

    @NotEmpty
    private List<String> scheduleIds; // supports round trip

    @NotEmpty
    private List<PassengerRequest> passengers;
}

