package com.saiteja.flightbookingwebfluxmongo.repository;

import com.saiteja.flightbookingwebfluxmongo.model.Booking;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {

    Mono<Booking> findByPnr(String pnr);

    Flux<Booking> findByContactEmail(String email);
}
