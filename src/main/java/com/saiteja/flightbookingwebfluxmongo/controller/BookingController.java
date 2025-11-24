package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.ApiResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.ticket.TicketResponse;
import com.saiteja.flightbookingwebfluxmongo.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking/{scheduleId}")
    public Mono<ResponseEntity<TicketResponse>> bookFlight(
            @PathVariable String scheduleId,
            @Valid @RequestBody BookingCreateRequest request
    ) {
        request.setScheduleIds(List.of(scheduleId));
        return bookingService.createBooking(request)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<ResponseEntity<ApiResponse>> cancelBooking(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr)
                .map(msg -> ResponseEntity.status(200).body(ApiResponse.builder().message(msg).build()));
    }

}
