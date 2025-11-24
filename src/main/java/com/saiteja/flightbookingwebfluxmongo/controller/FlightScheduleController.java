package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightSearchRequest;
import com.saiteja.flightbookingwebfluxmongo.service.FlightScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight/admin")
@RequiredArgsConstructor
@Validated
public class FlightScheduleController {

    private final FlightScheduleService flightScheduleService;

    @PostMapping("/inventory")
    public Mono<ResponseEntity<FlightScheduleResponse>> addInventory(@Valid @RequestBody FlightScheduleCreateRequest request) {
        return flightScheduleService.createSchedule(request)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @PostMapping("/search")
    public Mono<ResponseEntity<Flux<FlightScheduleResponse>>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        return Mono.just(
                ResponseEntity.ok(
                        flightScheduleService.searchFlights(
                                request.getOriginAirport().trim().toUpperCase(),
                                request.getDestinationAirport().trim().toUpperCase(),
                                request.getFlightDate()
                        )
                )
        );
    }

}
