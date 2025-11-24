package com.saiteja.flightbookingwebfluxmongo.repository;

import com.saiteja.flightbookingwebfluxmongo.model.User;
import com.saiteja.flightbookingwebfluxmongo.model.enums.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=6.0.2")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll().block();
    }

    @Test
    void findByEmailReturnsPersistedUser() {
        User user = new User();
        user.setEmail("repo-user@example.com");
        user.setPassword("secret");
        user.setRole(UserRole.USER);

        StepVerifier.create(userRepository.save(user)
                        .flatMap(saved -> userRepository.findByEmail("repo-user@example.com")))
                .assertNext(found -> {
                    assertThat(found.getEmail()).isEqualTo("repo-user@example.com");
                    assertThat(found.getRole()).isEqualTo(UserRole.USER);
                })
                .verifyComplete();
    }

    @Test
    void existsByEmailReflectsDatabaseState() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setPassword("secret");
        user.setRole(UserRole.USER);

        StepVerifier.create(userRepository.existsByEmail("exists@example.com"))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(userRepository.save(user)
                        .then(userRepository.existsByEmail("exists@example.com")))
                .expectNext(true)
                .verifyComplete();
    }
}

