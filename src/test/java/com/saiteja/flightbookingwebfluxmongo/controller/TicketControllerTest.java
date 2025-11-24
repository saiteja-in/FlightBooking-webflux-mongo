package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.ticket.TicketResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.GlobalExceptionHandler;
import com.saiteja.flightbookingwebfluxmongo.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        TicketController controller = new TicketController(ticketService);
        webTestClient = WebTestClient.bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .configureClient()
                .baseUrl("/api/v1.0/flight")
                .build();
    }

    @Test
    void getTicketByPnrReturnsResponse() {
        TicketResponse response = TicketResponse.builder()
                .pnr("PNR001")
                .bookingId("booking-1")
                .issuedAt(LocalDateTime.now())
                .build();

        when(ticketService.getTicketByPnr("PNR001")).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/ticket/PNR001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR001");
    }

    @Test
    void getTicketHandlesNotFound() {
        when(ticketService.getTicketByPnr("MISSING"))
                .thenReturn(Mono.error(new com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException("Ticket not found")));

        webTestClient.get()
                .uri("/ticket/MISSING")
                .exchange()
                .expectStatus().isNotFound();
    }
}

