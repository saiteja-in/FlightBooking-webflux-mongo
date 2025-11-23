package com.saiteja.flightbookingwebfluxmongo.service;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface FlightScheduleService {

    Flux<FlightScheduleResponse> searchFlights(String origin, String destination, LocalDate date);
    Mono<FlightScheduleResponse> createSchedule(FlightScheduleCreateRequest request);

    Mono<FlightScheduleResponse> getScheduleById(String id);
}
