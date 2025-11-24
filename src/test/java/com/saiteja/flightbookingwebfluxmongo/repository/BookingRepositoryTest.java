package com.saiteja.flightbookingwebfluxmongo.repository;

import com.saiteja.flightbookingwebfluxmongo.model.Booking;
import com.saiteja.flightbookingwebfluxmongo.model.Passenger;
import com.saiteja.flightbookingwebfluxmongo.model.enums.BookingStatus;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=6.0.2")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll().block();
    }

    @Test
    void findByPnrReturnsBooking() {
        Booking booking = booking("PNR001", "user@example.com");

        StepVerifier.create(bookingRepository.save(booking)
                        .then(bookingRepository.findByPnr("PNR001")))
                .assertNext(found -> assertThat(found.getContactEmail()).isEqualTo("user@example.com"))
                .verifyComplete();
    }

    @Test
    void findByContactEmailReturnsFlux() {
        Booking first = booking("PNR100", "history@example.com");
        Booking second = booking("PNR101", "history@example.com");

        StepVerifier.create(bookingRepository.save(first)
                        .then(bookingRepository.save(second))
                        .thenMany(bookingRepository.findByContactEmail("history@example.com")))
                .expectNextCount(2)
                .verifyComplete();
    }

    private Booking booking(String pnr, String email) {
        return Booking.builder()
                .pnr(pnr)
                .contactEmail(email)
                .scheduleIds(List.of("schedule-1"))
                .passengers(List.of(
                        Passenger.builder()
                                .fullName("John Doe")
                                .gender(Gender.MALE)
                                .age(30)
                                .seatNumber("1A")
                                .mealOption(MealOption.VEG)
                                .build()
                ))
                .status(BookingStatus.CONFIRMED)
                .build();
    }
}

