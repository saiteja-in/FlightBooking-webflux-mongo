package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.passenger.PassengerResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.BadRequestException;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.Booking;
import com.saiteja.flightbookingwebfluxmongo.model.FlightSchedule;
import com.saiteja.flightbookingwebfluxmongo.model.Passenger;
import com.saiteja.flightbookingwebfluxmongo.model.enums.BookingStatus;
import com.saiteja.flightbookingwebfluxmongo.repository.BookingRepository;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightScheduleRepository;
import com.saiteja.flightbookingwebfluxmongo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightScheduleRepository flightScheduleRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<BookingResponse> createBooking(BookingCreateRequest request) {

        return validateAndLockSeats(request)
                .flatMap(flightSchedule -> {
                    String pnr = generatePNR();

                    Booking booking = Booking.builder()
                            .pnr(pnr)
                            .contactEmail(request.getContactEmail())
                            .scheduleIds(request.getScheduleIds())
                            .passengers(mapPassengers(request))
                            .status(BookingStatus.CONFIRMED)
                            .build();

                    return bookingRepository.save(booking)
                            .map(this::toResponse);
                });
    }

    private Mono<FlightSchedule> validateAndLockSeats(BookingCreateRequest request) {

        return flightScheduleRepository.findById(request.getScheduleIds().get(0))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Flight schedule not found")))
                .flatMap(schedule -> {
                    if (schedule.getAvailableSeats() < request.getPassengers().size()) {
                        return Mono.error(new BadRequestException("Not enough seats available"));
                    }

                    Query query = Query.query(Criteria.where("_id").is(schedule.getId())
                            .and("availableSeats").gte(request.getPassengers().size()));

                    List<String> seatNumbers = request.getPassengers().stream()
                            .map(p -> p.getSeatNumber())
                            .toList();

                    Update update = new Update()
                            .push("bookedSeats").each(seatNumbers)
                            .inc("availableSeats", -seatNumbers.size());

                    return mongoTemplate.findAndModify(query, update, FlightSchedule.class)
                            .switchIfEmpty(Mono.error(new BadRequestException("Seat allocation conflict. Try another seat.")));
                });
    }

    @Override
    public Mono<BookingResponse> getBookingByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found")))
                .map(this::toResponse);
    }

    @Override
    public Flux<BookingResponse> getBookingsByEmail(String email) {
        return bookingRepository.findByContactEmail(email)
                .map(this::toResponse);
    }

    @Override
    public Mono<String> cancelBooking(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found")))
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    return bookingRepository.save(booking).thenReturn("Cancelled");
                });
    }

    private List<Passenger> mapPassengers(BookingCreateRequest request) {
        return request.getPassengers().stream()
                .map(p -> Passenger.builder()
                        .fullName(p.getFullName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .mealOption(p.getMealOption())
                        .seatNumber(p.getSeatNumber())
                        .build())
                .toList();
    }

    private BookingResponse toResponse(Booking booking) {
        List<PassengerResponse> passengers = booking.getPassengers().stream()
                .map(p -> PassengerResponse.builder()
                        .fullName(p.getFullName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .mealOption(p.getMealOption())
                        .build())
                .toList();

        return BookingResponse.builder()
                .pnr(booking.getPnr())
                .contactEmail(booking.getContactEmail())
                .scheduleIds(booking.getScheduleIds())
                .passengers(passengers)
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    private String generatePNR() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
