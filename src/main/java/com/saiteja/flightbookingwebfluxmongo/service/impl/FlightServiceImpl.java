package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.DuplicateResourceException;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.Flight;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightRepository;
import com.saiteja.flightbookingwebfluxmongo.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public Mono<FlightResponse> createFlight(FlightCreateRequest request) {
        return flightRepository.existsByFlightNumber(request.getFlightNumber())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException("Flight already exists with number: " + request.getFlightNumber()));
                    }

                    Flight flight = Flight.builder()
                            .flightNumber(request.getFlightNumber().trim())
                            .airline(request.getAirline())
                            .originAirport(request.getOriginAirport().toUpperCase())
                            .destinationAirport(request.getDestinationAirport().toUpperCase())
                            .seatCapacity(request.getSeatCapacity())
                            .build();

                    return flightRepository.save(flight)
                            .map(this::toResponse);
                });
    }

    @Override
    public Flux<FlightResponse> getAllFlights() {
        return flightRepository.findAll()
                .map(this::toResponse);
    }

    @Override
    public Mono<FlightResponse> getFlightById(String id) {
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Flight not found with id: " + id)))
                .map(this::toResponse);
    }

    @Override
    public Mono<String> deleteFlight(String id) {
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Flight not found: " + id)))
                .flatMap(flight -> flightRepository.delete(flight).thenReturn("Deleted"));
    }

    private FlightResponse toResponse(Flight flight) {
        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airline(flight.getAirline())
                .originAirport(flight.getOriginAirport())
                .destinationAirport(flight.getDestinationAirport())
                .seatCapacity(flight.getSeatCapacity())
                .build();
    }
}
