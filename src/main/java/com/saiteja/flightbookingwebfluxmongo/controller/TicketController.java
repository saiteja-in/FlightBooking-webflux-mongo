package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.ticket.TicketResponse;
import com.saiteja.flightbookingwebfluxmongo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/ticket/{pnr}")
    public Mono<ResponseEntity<TicketResponse>> getTicketByPnr(@PathVariable String pnr) {
        return ticketService.getTicketByPnr(pnr)
                .map(ResponseEntity::ok);
    }

}
