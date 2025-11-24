package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightResponse;
import com.saiteja.flightbookingwebfluxmongo.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight/admin/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public Mono<ResponseEntity<FlightResponse>> createFlight(@Valid @RequestBody FlightCreateRequest request) {
        return flightService.createFlight(request)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<FlightResponse>>> getAllFlights() {
        return Mono.just(ResponseEntity.ok(flightService.getAllFlights()));
    }

    @GetMapping("/{flightNumber}")
    public Mono<ResponseEntity<FlightResponse>> getFlight(@PathVariable String flightNumber) {
        return flightService.getFlightByFlightNumber(flightNumber)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteFlight(@PathVariable String id) {
        return flightService.deleteFlight(id)
                .map(message -> ResponseEntity.status(204).body(message));
    }

}
