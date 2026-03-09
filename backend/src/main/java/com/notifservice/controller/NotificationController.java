package com.notifservice.controller;

import com.notifservice.kafka.NotificationProducer;
import com.notifservice.model.Notification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationProducer producer;

    @PostMapping("/publish")
    public Mono<ResponseEntity<String>> publish(@Valid @RequestBody PublishRequest request) {
        Notification notification = Notification.builder()
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .source(request.getSource())
                .timestamp(LocalDateTime.now())
                .build();

        producer.publish(notification);

        return Mono.just(ResponseEntity.ok("Notification published: " + notification.getId()));
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        return Mono.just(ResponseEntity.ok("Notification service is up"));
    }

    @Data
    public static class PublishRequest {
        @NotBlank
        private String type;
        @NotBlank
        private String title;
        @NotBlank
        private String message;
        private String source = "manual";
    }
}