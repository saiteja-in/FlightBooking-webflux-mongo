package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.booking.BookingCreateRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.ticket.TicketResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.GlobalExceptionHandler;
import com.saiteja.flightbookingwebfluxmongo.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        BookingController controller = new BookingController(bookingService);
        webTestClient = WebTestClient.bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .validator(validator)
                .configureClient()
                .baseUrl("/api/v1.0/flight")
                .build();
    }

    @Test
    void bookFlightReturnsTicketResponse() {
        TicketResponse ticketResponse = TicketResponse.builder()
                .pnr("PNR123")
                .bookingId("booking-1")
                .build();

        when(bookingService.createBooking(any(BookingCreateRequest.class)))
                .thenReturn(Mono.just(ticketResponse));

        webTestClient.post()
                .uri("/booking/123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "contactEmail": "user@example.com",
                          "passengers": [
                            {
                              "fullName": "John Doe",
                              "gender": "MALE",
                              "age": 30,
                              "seatNumber": "1A",
                              "mealOption": "VEG"
                            }
                          ]
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR123");
    }

    @Test
    void cancelBookingDelegatesToService() {
        when(bookingService.cancelBooking("PNR123")).thenReturn(Mono.just("Booking cancelled"));

        webTestClient.delete()
                .uri("/booking/cancel/PNR123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Booking cancelled");
    }
}

