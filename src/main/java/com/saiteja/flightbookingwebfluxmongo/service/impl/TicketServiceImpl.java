package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.passenger.PassengerResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.ticket.TicketResponse;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.Ticket;
import com.saiteja.flightbookingwebfluxmongo.model.enums.TicketStatus;
import com.saiteja.flightbookingwebfluxmongo.repository.BookingRepository;
import com.saiteja.flightbookingwebfluxmongo.repository.TicketRepository;
import com.saiteja.flightbookingwebfluxmongo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Mono<TicketResponse> generateTicket(String bookingId) {

        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found")))
                .flatMap(booking -> {

                    Ticket ticket = Ticket.builder()
                            .pnr(booking.getPnr())
                            .bookingId(booking.getId())
                            .scheduleId(booking.getScheduleIds().get(0))
                            .passengers(booking.getPassengers())
                            .issuedAt(LocalDateTime.now())
                            .build();

                    return ticketRepository.save(ticket)
                            .map(this::toResponse);
                });
    }

    @Override
    public Mono<TicketResponse> getTicketByPnr(String pnr) {
        return ticketRepository.findByPnr(pnr)
                .filter(ticket -> ticket.getStatus() != TicketStatus.CANCELLED)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ticket not found")))
                .map(this::toResponse);
    }


    private TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .pnr(ticket.getPnr())
                .bookingId(ticket.getBookingId())
                .issuedAt(ticket.getIssuedAt())
                .passengers(
                        ticket.getPassengers()
                                .stream()
                                .map(p -> PassengerResponse.builder()
                                        .fullName(p.getFullName())
                                        .gender(p.getGender())
                                        .age(p.getAge())
                                        .seatNumber(p.getSeatNumber())
                                        .mealOption(p.getMealOption())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
