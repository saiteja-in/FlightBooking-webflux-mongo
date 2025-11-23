package com.saiteja.flightbookingwebfluxmongo.service;

import com.saiteja.flightbookingwebfluxmongo.dto.ticket.TicketResponse;
import reactor.core.publisher.Mono;

public interface TicketService {
    Mono<TicketResponse> generateTicket(String bookingId);
    Mono<TicketResponse> getTicketByPnr(String pnr);
}
