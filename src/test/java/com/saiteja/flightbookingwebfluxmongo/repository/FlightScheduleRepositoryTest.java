package com.saiteja.flightbookingwebfluxmongo.repository;

import com.saiteja.flightbookingwebfluxmongo.model.FlightSchedule;
import com.saiteja.flightbookingwebfluxmongo.model.enums.FlightStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=6.0.2")
class FlightScheduleRepositoryTest {

    @Autowired
    private FlightScheduleRepository flightScheduleRepository;

    @AfterEach
    void tearDown() {
        flightScheduleRepository.deleteAll().block();
    }

    @Test
    void findByFlightIdAndDateReturnsMatchingSchedules() {
        FlightSchedule matching = schedule("flight-1", LocalDate.of(2035, 1, 1));
        FlightSchedule otherDate = schedule("flight-1", LocalDate.of(2035, 1, 2));
        FlightSchedule otherFlight = schedule("flight-2", LocalDate.of(2035, 1, 1));

        StepVerifier.create(
                        flightScheduleRepository.save(matching)
                                .then(flightScheduleRepository.save(otherDate))
                                .then(flightScheduleRepository.save(otherFlight))
                                .thenMany(flightScheduleRepository.findByFlightIdAndFlightDate("flight-1", LocalDate.of(2035, 1, 1)))
                )
                .expectNextMatches(schedule -> schedule.getFlightId().equals("flight-1")
                        && schedule.getFlightDate().equals(LocalDate.of(2035, 1, 1)))
                .verifyComplete();
    }

    private FlightSchedule schedule(String flightId, LocalDate date) {
        return FlightSchedule.builder()
                .flightId(flightId)
                .flightDate(date)
                .departureTime(LocalTime.of(10, 0))
                .arrivalTime(LocalTime.of(12, 0))
                .fare(BigDecimal.valueOf(9999.99))
                .totalSeats(180)
                .availableSeats(180)
                .status(FlightStatus.SCHEDULED)
                .build();
    }
}

