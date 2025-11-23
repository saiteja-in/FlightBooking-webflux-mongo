package com.saiteja.flightbookingwebfluxmongo.repository;

import com.saiteja.flightbookingwebfluxmongo.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
}
