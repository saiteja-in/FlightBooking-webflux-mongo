package com.saiteja.flightbookingwebfluxmongo.repository;

import com.saiteja.flightbookingwebfluxmongo.model.Flight;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Airline;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=6.0.2")
class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepository;

    @AfterEach
    void cleanUp() {
        flightRepository.deleteAll().block();
    }

    @Test
    void findByFlightNumberReturnsSavedEntity() {
        Flight flight = flight("AI101", "DEL", "BLR");

        StepVerifier.create(flightRepository.save(flight)
                        .then(flightRepository.findByFlightNumber("AI101")))
                .assertNext(found -> assertThat(found.getDestinationAirport()).isEqualTo("BLR"))
                .verifyComplete();
    }

    @Test
    void existsByFlightNumberReflectsData() {
        StepVerifier.create(flightRepository.existsByFlightNumber("AI101"))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(flightRepository.save(flight("AI101", "DEL", "BLR"))
                        .then(flightRepository.existsByFlightNumber("AI101")))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByOriginAndDestinationFiltersResults() {
        Flight match = flight("AI101", "DEL", "BLR");
        Flight other = flight("AI102", "DEL", "MAA");

        StepVerifier.create(flightRepository.save(match)
                        .then(flightRepository.save(other))
                        .thenMany(flightRepository.findByOriginAirportAndDestinationAirport("DEL", "BLR")))
                .expectNextMatches(f -> f.getFlightNumber().equals("AI101"))
                .verifyComplete();
    }

    private Flight flight(String flightNumber, String origin, String destination) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setAirline(Airline.AIR_INDIA);
        flight.setOriginAirport(origin);
        flight.setDestinationAirport(destination);
        flight.setSeatCapacity(180);
        return flight;
    }
}

