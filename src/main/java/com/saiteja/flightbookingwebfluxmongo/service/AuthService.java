package com.saiteja.flightbookingwebfluxmongo.service;

import com.saiteja.flightbookingwebfluxmongo.dto.auth.AuthResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserLoginRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserRegisterRequest;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthResponse> register(UserRegisterRequest request);

    Mono<AuthResponse> login(UserLoginRequest request);
}
