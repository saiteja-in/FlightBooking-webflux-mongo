package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.auth.AuthResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserLoginRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserRegisterRequest;
import com.saiteja.flightbookingwebfluxmongo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        return authService.register(request)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok);
    }
}
