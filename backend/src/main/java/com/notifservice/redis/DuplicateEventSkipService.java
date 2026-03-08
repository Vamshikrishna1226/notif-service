package com.notifservice.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateEventSkipService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${app.redis.dedup-ttl-seconds}")
    private long dedupTtlSeconds;

    private static final String KEY_PREFIX = "notif:seen:";

    public Mono<Boolean> isNewEvent(String eventId) {
        String key = KEY_PREFIX + eventId;
        return redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(dedupTtlSeconds))
                .doOnNext(isNew -> {
                    if (!isNew) {
                        log.debug("Duplicate event skipped: {}", eventId);
                    }
                });
    }
}