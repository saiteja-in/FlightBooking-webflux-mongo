package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.auth.AuthResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserLoginRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserRegisterRequest;
import com.saiteja.flightbookingwebfluxmongo.exception.BadRequestException;
import com.saiteja.flightbookingwebfluxmongo.exception.DuplicateResourceException;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.User;
import com.saiteja.flightbookingwebfluxmongo.model.enums.UserRole;
import com.saiteja.flightbookingwebfluxmongo.repository.UserRepository;
import com.saiteja.flightbookingwebfluxmongo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Mono<AuthResponse> register(UserRegisterRequest request) {

        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists)
                        throw new DuplicateResourceException("Email already registered");

                    User user = new User();
                    user.setEmail(request.getEmail().trim());
                    user.setPassword(encoder.encode(request.getPassword()));
                    user.setRole(UserRole.USER);

                    return userRepository.save(user)
                            .map(saved -> AuthResponse.builder()
                                    .message("Registration successful")
                                    .email(saved.getEmail())
                                    .role(saved.getRole().name())
                                    .build());
                });
    }

    @Override
    public Mono<AuthResponse> login(UserLoginRequest request) {

        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
                .flatMap(user -> {
                    if (!encoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new BadRequestException("Invalid credentials"));
                    }

                    return Mono.just(AuthResponse.builder()
                            .message("Login successful")
                            .email(user.getEmail())
                            .role(user.getRole().name())
                            .build());
                });
    }
}
