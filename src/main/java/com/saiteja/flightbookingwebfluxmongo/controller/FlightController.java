package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightResponse;
import com.saiteja.flightbookingwebfluxmongo.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight/admin/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public Mono<FlightResponse> createFlight(@Valid @RequestBody FlightCreateRequest request) {
        return flightService.createFlight(request);
    }

    @GetMapping
    public Flux<FlightResponse> getAllFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/{flightNumber}")
    public Mono<FlightResponse> getFlight(@PathVariable String flightNumber) {
        return flightService.getFlightByFlightNumber(flightNumber);
    }


    @DeleteMapping("/{id}")
    public Mono<String> deleteFlight(@PathVariable String id) {
        return flightService.deleteFlight(id); // returns "Deleted"
    }
}
