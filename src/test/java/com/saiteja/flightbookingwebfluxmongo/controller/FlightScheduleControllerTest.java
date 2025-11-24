package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightScheduleResponse;
import com.saiteja.flightbookingwebfluxmongo.service.FlightScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightScheduleControllerTest {

    @Mock
    private FlightScheduleService flightScheduleService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        FlightScheduleController controller = new FlightScheduleController(flightScheduleService);
        webTestClient = WebTestClient.bindToController(controller)
                .validator(validator)
                .configureClient()
                .baseUrl("/api/v1.0/flight/admin")
                .build();
    }

    @Test
    void addInventoryPersistsSchedule() {
        FlightScheduleResponse response = FlightScheduleResponse.builder()
                .scheduleId("schedule-1")
                .flightNumber("AI101")
                .departureTime(LocalTime.NOON)
                .build();

        when(flightScheduleService.createSchedule(any(FlightScheduleCreateRequest.class)))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "flightNumber": "AI101",
                          "flightDate": "2035-01-01",
                          "departureTime": "10:00:00",
                          "arrivalTime": "12:00:00",
                          "fare": 12000.00
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.flightNumber").isEqualTo("AI101");
    }

    @Test
    void searchFlightsReturnsFlux() {
        FlightScheduleResponse response = FlightScheduleResponse.builder()
                .scheduleId("schedule-1")
                .flightNumber("AI101")
                .originAirport("DEL")
                .destinationAirport("BLR")
                .flightDate(LocalDate.now().plusDays(1))
                .build();

        when(flightScheduleService.searchFlights("DEL", "BLR", LocalDate.parse("2035-01-01")))
                .thenReturn(Flux.just(response));

        webTestClient.post()
                .uri("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "originAirport": "DEL",
                          "destinationAirport": "BLR",
                          "flightDate": "2035-01-01"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].flightNumber").isEqualTo("AI101")
                .jsonPath("$.length()").isEqualTo(1);
    }

    @Test
    void searchFlightsRequiresValidPayload() {
        webTestClient.post()
                .uri("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "originAirport": "",
                          "destinationAirport": "",
                          "flightDate": null
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }
}

