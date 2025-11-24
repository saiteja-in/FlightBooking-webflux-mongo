package com.saiteja.flightbookingwebfluxmongo.dto.booking;

import com.saiteja.flightbookingwebfluxmongo.dto.passenger.PassengerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BookingCreateRequest {

    @NotBlank @Email
    private String contactEmail;

    private List<@NotBlank String> scheduleIds; // supports round trip

    @NotEmpty
    private List<@Valid PassengerRequest> passengers;
}

