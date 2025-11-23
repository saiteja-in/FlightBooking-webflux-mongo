package com.saiteja.flightbookingwebfluxmongo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return Mono.just(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Mono<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        return Mono.just(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.CONFLICT.value(),
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return Mono.just(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public Mono<Map<String, Object>> handleGeneral(Exception ex) {
        return Mono.just(Map.of(
                    "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Something went wrong"
        ));
    }
}
