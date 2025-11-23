package com.saiteja.flightbookingwebfluxmongo.service;

import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    Mono<BookingResponse> createBooking(BookingCreateRequest request);

    Mono<BookingResponse> getBookingByPnr(String pnr);

    Flux<BookingResponse> getBookingsByEmail(String email);

    Mono<String> cancelBooking(String pnr);
}
