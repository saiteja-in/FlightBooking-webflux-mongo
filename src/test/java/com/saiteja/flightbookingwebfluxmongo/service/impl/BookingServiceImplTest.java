package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.passenger.PassengerRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.ticket.TicketResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.BadRequestException;
import com.saiteja.flightbookingwebfluxmongo.model.Booking;
import com.saiteja.flightbookingwebfluxmongo.model.FlightSchedule;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import com.saiteja.flightbookingwebfluxmongo.repository.BookingRepository;
import com.saiteja.flightbookingwebfluxmongo.repository.FlightScheduleRepository;
import com.saiteja.flightbookingwebfluxmongo.repository.TicketRepository;
import com.saiteja.flightbookingwebfluxmongo.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private FlightScheduleRepository flightScheduleRepository;
    @Mock
    private ReactiveMongoTemplate mongoTemplate;
    @Mock
    private TicketService ticketService;
    @Mock
    private TicketRepository ticketRepository;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                flightScheduleRepository,
                mongoTemplate,
                ticketService,
                ticketRepository
        );
    }

    @Test
    void createBookingWithoutScheduleIdsReturnsBadRequest() {
        BookingCreateRequest request = baseRequest();
        request.setScheduleIds(null); // simulate missing schedule ids

        StepVerifier.create(bookingService.createBooking(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadRequestException);
                    assertThat(error.getMessage()).contains("At least one schedule id");
                })
                .verify();
    }

    @Test
    void createBookingFailsWhenSeatsAreUnavailable() {
        BookingCreateRequest request = baseRequest();
        request.setScheduleIds(List.of("schedule-1"));
        request.setPassengers(List.of(passenger("John Doe", "1A"), passenger("Jane Doe", "1B")));

        FlightSchedule schedule = FlightSchedule.builder()
                .id("schedule-1")
                .availableSeats(1)
                .bookedSeats(new ArrayList<>())
                .build();

        when(flightScheduleRepository.findById("schedule-1")).thenReturn(Mono.just(schedule));

        StepVerifier.create(bookingService.createBooking(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadRequestException);
                    assertThat(error.getMessage()).contains("Not enough seats");
                })
                .verify();
    }

    @Test
    void createBookingSucceedsAndGeneratesTicket() {
        BookingCreateRequest request = baseRequest();
        request.setScheduleIds(List.of("schedule-1"));

        FlightSchedule schedule = FlightSchedule.builder()
                .id("schedule-1")
                .availableSeats(5)
                .bookedSeats(new ArrayList<>())
                .build();

        TicketResponse ticketResponse = TicketResponse.builder()
                .pnr("PNR001")
                .bookingId("booking-123")
                .passengers(List.of())
                .build();

        when(flightScheduleRepository.findById("schedule-1")).thenReturn(Mono.just(schedule));
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(FlightSchedule.class)))
                .thenReturn(Mono.just(schedule));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId("booking-123");
            return Mono.just(booking);
        });
        when(ticketService.generateTicket("booking-123")).thenReturn(Mono.just(ticketResponse));

        StepVerifier.create(bookingService.createBooking(request))
                .expectNext(ticketResponse)
                .verifyComplete();
    }

    private BookingCreateRequest baseRequest() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setContactEmail("user@example.com");
        request.setPassengers(List.of(passenger("John Doe", "1A")));
        return request;
    }

    private PassengerRequest passenger(String fullName, String seatNumber) {
        PassengerRequest passenger = new PassengerRequest();
        passenger.setFullName(fullName);
        passenger.setGender(Gender.MALE);
        passenger.setAge(30);
        passenger.setSeatNumber(seatNumber);
        passenger.setMealOption(MealOption.VEG);
        return passenger;
    }
}

