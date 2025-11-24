package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.Flight;
import com.saiteja.flightbookingwebfluxmongo.model.FlightSchedule;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Airline;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightRepository;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightScheduleServiceImplTest {

    @Mock
    private FlightScheduleRepository flightScheduleRepository;
    @Mock
    private FlightRepository flightRepository;

    private FlightScheduleServiceImpl flightScheduleService;

    @BeforeEach
    void setUp() {
        flightScheduleService = new FlightScheduleServiceImpl(flightScheduleRepository, flightRepository);
    }

    @Test
    void searchFlightsErrorsWhenNoMatches() {
        when(flightRepository.findByOriginAirportAndDestinationAirport("DEL", "BLR"))
                .thenReturn(Flux.empty());

        StepVerifier.create(flightScheduleService.searchFlights("DEL", "BLR", LocalDate.now()))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void createScheduleErrorsWhenFlightMissing() {
        FlightScheduleCreateRequest request = createRequest();
        when(flightRepository.findByFlightNumber("AI101")).thenReturn(Mono.empty());

        StepVerifier.create(flightScheduleService.createSchedule(request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void createSchedulePersistsAndReturnsResponse() {
        FlightScheduleCreateRequest request = createRequest();
        Flight flight = Flight.builder()
                .id("flight-1")
                .flightNumber("AI101")
                .airline(Airline.AIR_INDIA)
                .originAirport("DEL")
                .destinationAirport("BLR")
                .seatCapacity(180)
                .build();

        FlightSchedule savedSchedule = FlightSchedule.builder()
                .id("schedule-1")
                .flightId("flight-1")
                .flightDate(request.getFlightDate())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .availableSeats(180)
                .build();

        when(flightRepository.findByFlightNumber("AI101")).thenReturn(Mono.just(flight));
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(Mono.just(savedSchedule));

        StepVerifier.create(flightScheduleService.createSchedule(request))
                .assertNext(response -> {
                    assertThat(response.getScheduleId()).isEqualTo("schedule-1");
                    assertThat(response.getFlightNumber()).isEqualTo("AI101");
                    assertThat(response.getAvailableSeats()).isEqualTo(180);
                })
                .verifyComplete();
    }

    @Test
    void getScheduleByIdCombinesFlightDetails() {
        FlightSchedule schedule = FlightSchedule.builder()
                .id("schedule-1")
                .flightId("flight-1")
                .flightDate(LocalDate.now())
                .departureTime(LocalTime.NOON)
                .arrivalTime(LocalTime.MIDNIGHT)
                .availableSeats(100)
                .build();
        Flight flight = Flight.builder()
                .id("flight-1")
                .flightNumber("AI101")
                .airline(Airline.AIR_INDIA)
                .originAirport("DEL")
                .destinationAirport("BLR")
                .build();

        when(flightScheduleRepository.findById("schedule-1")).thenReturn(Mono.just(schedule));
        when(flightRepository.findById("flight-1")).thenReturn(Mono.just(flight));

        StepVerifier.create(flightScheduleService.getScheduleById("schedule-1"))
                .expectNextMatches(resp -> resp.getFlightNumber().equals("AI101") &&
                        resp.getOriginAirport().equals("DEL"))
                .verifyComplete();
    }

    private FlightScheduleCreateRequest createRequest() {
        FlightScheduleCreateRequest request = new FlightScheduleCreateRequest();
        request.setFlightNumber("AI101");
        request.setFlightDate(LocalDate.now());
        request.setDepartureTime(LocalTime.NOON);
        request.setArrivalTime(LocalTime.MIDNIGHT);
        request.setFare(BigDecimal.valueOf(9999.99));
        return request;
    }
}

