package com.notifservice;

import com.notifservice.model.Notification;
import com.notifservice.redis.DuplicateEventSkipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicateEventSkipServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOps;

    @InjectMocks
    private DuplicateEventSkipService duplicateEventSkipService;

    @Test
    void newEventShouldPassThrough() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(duplicateEventSkipService.isNewEvent("abc-001"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void sameEventShouldBeBlocked() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(false));

        StepVerifier.create(duplicateEventSkipService.isNewEvent("abc-001"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void eachNotificationGetsItsOwnId() {
        Notification n1 = Notification.builder().type("INFO").title("Test").message("Hello").build();
        Notification n2 = Notification.builder().type("INFO").title("Test").message("Hello").build();
        assert !n1.getId().equals(n2.getId());
    }
}