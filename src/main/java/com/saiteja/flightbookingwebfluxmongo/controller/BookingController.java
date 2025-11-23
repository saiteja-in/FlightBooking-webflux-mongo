package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.ApiResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingResponse;
import com.saiteja.flightbookingwebfluxmongo.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking/{scheduleId}")
    public Mono<BookingResponse> bookFlight(@PathVariable String scheduleId,
                                            @RequestBody BookingCreateRequest request) {
        request.setScheduleIds(List.of(scheduleId));
        return bookingService.createBooking(request);
    }

    @GetMapping("/booking/history/{emailId}")
    public Flux<BookingResponse> getBookingHistory(@PathVariable String emailId) {
        return bookingService.getBookingsByEmail(emailId);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<ApiResponse> cancelBooking(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr)
                .map(msg -> ApiResponse.builder().message(msg).build());
    }
}
