package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.exception.DuplicateResourceException;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.Flight;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Airline;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    private FlightServiceImpl flightService;

    @BeforeEach
    void setUp() {
        flightService = new FlightServiceImpl(flightRepository);
    }

    @Test
    void createFlightFailsWhenFlightNumberExists() {
        FlightCreateRequest request = baseRequest();
        when(flightRepository.existsByFlightNumber(request.getFlightNumber())).thenReturn(Mono.just(true));

        StepVerifier.create(flightService.createFlight(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof DuplicateResourceException);
                    assertThat(error.getMessage()).contains("Flight already exists");
                })
                .verify();
    }

    @Test
    void createFlightPersistsAndNormalizesFields() {
        FlightCreateRequest request = baseRequest();
        request.setOriginAirport(" hyd ");
        request.setDestinationAirport(" del ");

        when(flightRepository.existsByFlightNumber(request.getFlightNumber())).thenReturn(Mono.just(false));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> {
            Flight flight = invocation.getArgument(0);
            flight.setId("flight-123");
            return Mono.just(flight);
        });

        StepVerifier.create(flightService.createFlight(request))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo("flight-123");
                    assertThat(response.getOriginAirport()).isEqualTo("HYD");
                    assertThat(response.getDestinationAirport()).isEqualTo("DEL");
                })
                .verifyComplete();
    }

    @Test
    void getFlightByFlightNumberErrorsWhenMissing() {
        when(flightRepository.findByFlightNumber("AB123")).thenReturn(Mono.empty());

        StepVerifier.create(flightService.getFlightByFlightNumber("AB123"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void getAllFlightsMapsResponses() {
        Flight first = Flight.builder().id("1").flightNumber("AB1").build();
        Flight second = Flight.builder().id("2").flightNumber("AB2").build();

        when(flightRepository.findAll()).thenReturn(Flux.just(first, second));

        StepVerifier.create(flightService.getAllFlights())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void deleteFlightRequiresExistingRecord() {
        when(flightRepository.findById("flight-1")).thenReturn(Mono.empty());

        StepVerifier.create(flightService.deleteFlight("flight-1"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    private FlightCreateRequest baseRequest() {
        FlightCreateRequest request = new FlightCreateRequest();
        request.setFlightNumber("AB123");
        request.setAirline(Airline.AIR_INDIA);
        request.setOriginAirport("DEL");
        request.setDestinationAirport("BLR");
        request.setSeatCapacity(150);
        return request;
    }
}

