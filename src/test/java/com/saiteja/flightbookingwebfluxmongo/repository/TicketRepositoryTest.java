package com.saiteja.flightbookingwebfluxmongo.repository;

import com.saiteja.flightbookingwebfluxmongo.model.Passenger;
import com.saiteja.flightbookingwebfluxmongo.model.Ticket;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import com.saiteja.flightbookingwebfluxmongo.model.enums.TicketStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=6.0.2")
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @AfterEach
    void clean() {
        ticketRepository.deleteAll().block();
    }

    @Test
    void findByPnrReturnsTicket() {
        Ticket ticket = ticket("PNR500", TicketStatus.ACTIVE);

        StepVerifier.create(ticketRepository.save(ticket)
                        .then(ticketRepository.findByPnr("PNR500")))
                .assertNext(found -> assertThat(found.getBookingId()).isEqualTo("booking-1"))
                .verifyComplete();
    }

    @Test
    void cancelledTicketIsPersistedWithStatus() {
        Ticket ticket = ticket("PNR600", TicketStatus.CANCELLED);

        StepVerifier.create(ticketRepository.save(ticket)
                        .then(ticketRepository.findByPnr("PNR600")))
                .assertNext(found -> assertThat(found.getStatus()).isEqualTo(TicketStatus.CANCELLED))
                .verifyComplete();
    }

    private Ticket ticket(String pnr, TicketStatus status) {
        return Ticket.builder()
                .pnr(pnr)
                .bookingId("booking-1")
                .scheduleId("schedule-1")
                .status(status)
                .passengers(List.of(
                        Passenger.builder()
                                .fullName("John Doe")
                                .gender(Gender.MALE)
                                .age(30)
                                .seatNumber("1A")
                                .mealOption(MealOption.VEG)
                                .build()
                ))
                .issuedAt(LocalDateTime.now())
                .build();
    }
}

