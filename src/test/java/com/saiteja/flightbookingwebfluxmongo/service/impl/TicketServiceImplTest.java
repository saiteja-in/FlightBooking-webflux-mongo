package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.Booking;
import com.saiteja.flightbookingwebfluxmongo.model.Passenger;
import com.saiteja.flightbookingwebfluxmongo.model.Ticket;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import com.saiteja.flightbookingwebfluxmongo.model.enums.TicketStatus;
import com.saiteja.flightbookingwebfluxmongo.repository.BookingRepository;
import com.saiteja.flightbookingwebfluxmongo.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private BookingRepository bookingRepository;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketServiceImpl(ticketRepository, bookingRepository);
    }

    @Test
    void generateTicketFailsWhenBookingMissing() {
        when(bookingRepository.findById("booking-1")).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.generateTicket("booking-1"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void generateTicketBuildsResponse() {
        Booking booking = Booking.builder()
                .id("booking-1")
                .pnr("PNR123")
                .contactEmail("user@example.com")
                .scheduleIds(List.of("schedule-1"))
                .passengers(List.of(passenger("John Doe", "1A")))
                .status(com.saiteja.flightbookingwebfluxmongo.model.enums.BookingStatus.CONFIRMED)
                .build();
        Ticket ticket = Ticket.builder()
                .id("ticket-1")
                .pnr("PNR123")
                .bookingId("booking-1")
                .scheduleId("schedule-1")
                .status(TicketStatus.ACTIVE)
                .passengers(booking.getPassengers())
                .issuedAt(LocalDateTime.now())
                .build();

        when(bookingRepository.findById("booking-1")).thenReturn(Mono.just(booking));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(Mono.just(ticket));

        StepVerifier.create(ticketService.generateTicket("booking-1"))
                .assertNext(response -> {
                    assertThat(response.getPnr()).isEqualTo("PNR123");
                    assertThat(response.getPassengers()).hasSize(1);
                })
                .verifyComplete();
    }

    @Test
    void getTicketByPnrFiltersCancelledTickets() {
        Ticket cancelled = Ticket.builder()
                .pnr("PNR123")
                .bookingId("booking-1")
                .scheduleId("schedule-1")
                .status(TicketStatus.CANCELLED)
                .passengers(List.of(passenger("John Doe", "1A")))
                .issuedAt(LocalDateTime.now())
                .build();

        when(ticketRepository.findByPnr("PNR123")).thenReturn(Mono.just(cancelled));

        StepVerifier.create(ticketService.getTicketByPnr("PNR123"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void getTicketByPnrReturnsActiveTicket() {
        Ticket ticket = Ticket.builder()
                .pnr("PNR123")
                .bookingId("booking-1")
                .scheduleId("schedule-1")
                .status(TicketStatus.ACTIVE)
                .passengers(List.of(passenger("John Doe", "1A")))
                .issuedAt(LocalDateTime.now())
                .build();

        when(ticketRepository.findByPnr("PNR123")).thenReturn(Mono.just(ticket));

        StepVerifier.create(ticketService.getTicketByPnr("PNR123"))
                .expectNextMatches(response -> response.getBookingId().equals("booking-1"))
                .verifyComplete();
    }

    private Passenger passenger(String name, String seatNumber) {
        return Passenger.builder()
                .fullName(name)
                .gender(Gender.MALE)
                .age(30)
                .seatNumber(seatNumber)
                .mealOption(MealOption.VEG)
                .build();
    }
}

