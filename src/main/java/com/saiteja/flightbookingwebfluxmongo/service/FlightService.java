package com.saiteja.flightbookingwebfluxmongo.service;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightService {

    Mono<FlightResponse> createFlight(FlightCreateRequest request);

    Flux<FlightResponse> getAllFlights();

    Mono<FlightResponse> getFlightById(String id);

    Mono<String> deleteFlight(String id);
}
