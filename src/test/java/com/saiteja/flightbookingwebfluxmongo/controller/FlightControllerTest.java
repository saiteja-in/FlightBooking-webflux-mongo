package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.flight.FlightResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.GlobalExceptionHandler;
import com.saiteja.flightbookingwebfluxmongo.model.enums.Airline;
import com.saiteja.flightbookingwebfluxmongo.service.FlightService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    @Mock
    private FlightService flightService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        FlightController controller = new FlightController(flightService);
        webTestClient = WebTestClient.bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .validator(validator)
                .configureClient()
                .baseUrl("/api/v1.0/flight/admin/flights")
                .build();
    }

    @Test
    void createFlightReturnsCreatedStatus() {
        FlightResponse response = FlightResponse.builder()
                .id("flight-1")
                .flightNumber("AI101")
                .airline(Airline.AIR_INDIA)
                .originAirport("DEL")
                .destinationAirport("BLR")
                .seatCapacity(180)
                .build();

        when(flightService.createFlight(any())).thenReturn(Mono.just(response));

        webTestClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "flightNumber": "AI101",
                          "airline": "AIR_INDIA",
                          "originAirport": "DEL",
                          "destinationAirport": "BLR",
                          "seatCapacity": 180
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.flightNumber").isEqualTo("AI101");
    }

    @Test
    void createFlightValidatesPayload() {
        webTestClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "airline": "AIR_INDIA"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getAllFlightsStreamsFlux() {
        FlightResponse first = FlightResponse.builder()
                .id("1")
                .flightNumber("AI101")
                .airline(Airline.AIR_INDIA)
                .originAirport("DEL")
                .destinationAirport("BLR")
                .seatCapacity(150)
                .build();
        FlightResponse second = FlightResponse.builder()
                .id("2")
                .flightNumber("AI102")
                .airline(Airline.AIR_INDIA)
                .originAirport("DEL")
                .destinationAirport("MAA")
                .seatCapacity(160)
                .build();

        when(flightService.getAllFlights()).thenReturn(Flux.just(first, second));

        webTestClient.get()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].flightNumber").isEqualTo("AI101");
    }

    @Test
    void getFlightByNumberReturnsResponse() {
        FlightResponse response = FlightResponse.builder()
                .id("flight-1")
                .flightNumber("AI101")
                .airline(Airline.AIR_INDIA)
                .originAirport("DEL")
                .destinationAirport("BLR")
                .seatCapacity(180)
                .build();

        when(flightService.getFlightByFlightNumber("AI101")).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/AI101")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.flightNumber").isEqualTo("AI101");
    }

    @Test
    void deleteFlightDelegatesToService() {
        when(flightService.deleteFlight(eq("flight-1"))).thenReturn(Mono.just("Deleted"));

        webTestClient.delete()
                .uri("/flight-1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(String.class)
                .isEqualTo("Deleted");
    }
}

