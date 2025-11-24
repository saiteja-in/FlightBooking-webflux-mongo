package com.saiteja.flightbookingwebfluxmongo.service.impl;

import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserLoginRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserRegisterRequest;
import com.saiteja.flightbookingwebfluxmongo.exception.BadRequestException;
import com.saiteja.flightbookingwebfluxmongo.exception.DuplicateResourceException;
import com.saiteja.flightbookingwebfluxmongo.exception.ResourceNotFoundException;
import com.saiteja.flightbookingwebfluxmongo.model.User;
import com.saiteja.flightbookingwebfluxmongo.model.enums.UserRole;
import com.saiteja.flightbookingwebfluxmongo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository);
    }

    @Test
    void registerFailsWhenEmailExists() {
        UserRegisterRequest request = registerRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(request))
                .expectError(DuplicateResourceException.class)
                .verify();
    }

    @Test
    void registerSavesUserAndReturnsResponse() {
        UserRegisterRequest request = registerRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId("user-1");
                    user.setRole(UserRole.USER);
                    return Mono.just(user);
                });

        StepVerifier.create(authService.register(request))
                .assertNext(response -> {
                    assertThat(response.getEmail()).isEqualTo(request.getEmail());
                    assertThat(response.getMessage()).contains("Registration successful");
                })
                .verifyComplete();
    }

    @Test
    void loginFailsWhenEmailMissing() {
        UserLoginRequest request = loginRequest();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(request))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void loginFailsWhenPasswordMismatch() {
        UserLoginRequest request = loginRequest();
        User user = new User();
        user.setEmail(request.getEmail());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode("another-password"));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Mono.just(user));

        StepVerifier.create(authService.login(request))
                .expectError(BadRequestException.class)
                .verify();
    }

    private UserRegisterRequest registerRequest() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("user@example.com");
        request.setPassword("Password123!");
        return request;
    }

    private UserLoginRequest loginRequest() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Password123!");
        return request;
    }
}

