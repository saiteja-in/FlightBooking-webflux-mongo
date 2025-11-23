package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.Flight;
import com.saiteja.flightbookingwebfluxmongo.model.FlightSchedule;
import com.saiteja.flightbookingwebfluxmongo.model.enums.FlightStatus;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightRepository;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightScheduleRepository;
import com.saiteja.flightbookingwebfluxmongo.service.FlightScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FlightScheduleServiceImpl implements FlightScheduleService {

    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightRepository flightRepository;

    @Override
    public Flux<FlightScheduleResponse> searchFlights(String origin, String destination, LocalDate date) {

        return flightRepository.findByOriginAirportAndDestinationAirport(origin, destination)
                .flatMap(flight ->
                        flightScheduleRepository.findByFlightIdAndFlightDate(flight.getId(), date)
                                .map(schedule -> toResponse(schedule, flight))
                )
                .switchIfEmpty(Flux.error(new ResourceNotFoundException("No schedule found for given criteria")));
    }

    @Override
    public Mono<FlightScheduleResponse> createSchedule(FlightScheduleCreateRequest request) {

        return flightRepository.findByFlightNumber(request.getFlightNumber().trim().toUpperCase())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Flight not found with number: " + request.getFlightNumber()
                )))
                .flatMap(flight -> {

                    FlightSchedule schedule = FlightSchedule.builder()
                            .flightId(flight.getId())
                            .flightDate(request.getFlightDate())
                            .departureTime(request.getDepartureTime())
                            .arrivalTime(request.getArrivalTime())
                            .fare(request.getFare())
                            .totalSeats(flight.getSeatCapacity())
                            .availableSeats(flight.getSeatCapacity())
                            .status(FlightStatus.SCHEDULED)
                            .build();

                    return flightScheduleRepository.save(schedule)
                            .map(saved -> toResponse(saved, flight));
                });
    }



    @Override
    public Mono<FlightScheduleResponse> getScheduleById(String id) {
        return flightScheduleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Flight schedule not found: " + id)))
                .flatMap(schedule ->
                        flightRepository.findById(schedule.getFlightId())
                                .map(flight -> toResponse(schedule, flight))
                );
    }

    private FlightScheduleResponse toResponse(FlightSchedule schedule, Flight flight) {
        return FlightScheduleResponse.builder()
                .scheduleId(schedule.getId())
                .flightNumber(flight.getFlightNumber())
                .airline(flight.getAirline().name())
                .originAirport(flight.getOriginAirport())
                .destinationAirport(flight.getDestinationAirport())
                .flightDate(schedule.getFlightDate())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .fare(schedule.getFare())
                .availableSeats(schedule.getAvailableSeats())
                .build();
    }
}
