package com.saiteja.flightbookingwebfluxmongo.dto.booking;

import com.saiteja.flightbookingwebfluxmongo.dto.passenger.PassengerResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {

    private String pnr;
    private String contactEmail;
    private List<String> scheduleIds;
    private List<PassengerResponse> passengers;
    private String status;
    @CreatedDate
    private LocalDateTime createdAt;
}
