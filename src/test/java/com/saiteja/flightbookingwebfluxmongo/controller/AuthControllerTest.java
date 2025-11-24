package com.saiteja.flightbookingwebfluxmongo.controller;

import com.saiteja.flightbookingwebfluxmongo.dto.auth.AuthResponse;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserLoginRequest;
import com.saiteja.flightbookingwebfluxmongo.dto.auth.UserRegisterRequest;
import com.saiteja.flightbookingwebfluxmongo.exception.GlobalExceptionHandler;
import com.saiteja.flightbookingwebfluxmongo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        AuthController authController = new AuthController(authService);
        this.webTestClient = WebTestClient.bindToController(authController)
                .controllerAdvice(new GlobalExceptionHandler())
                .validator(validator)
                .configureClient()
                .baseUrl("/api/v1.0/auth")
                .build();
    }

    @Test
    void registerReturnsCreatedStatus() {
        AuthResponse response = AuthResponse.builder()
                .message("Registration successful")
                .email("user@example.com")
                .role("USER")
                .build();

        when(authService.register(any(UserRegisterRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "email": "user@example.com",
                          "password": "secret"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo("user@example.com")
                .jsonPath("$.message").isEqualTo("Registration successful");
    }

    @Test
    void loginReturnsOkStatus() {
        AuthResponse response = AuthResponse.builder()
                .message("Login successful")
                .email("user@example.com")
                .role("USER")
                .build();

        when(authService.login(any(UserLoginRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "email": "user@example.com",
                          "password": "secret"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role").isEqualTo("USER");
    }

    @Test
    void loginValidationFailureReturnsBadRequest() {
        webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "password": "secret"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();

        Mockito.verifyNoInteractions(authService);
    }
}

