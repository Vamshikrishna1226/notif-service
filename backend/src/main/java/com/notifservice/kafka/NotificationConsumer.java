package com.notifservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifservice.model.Notification;
import com.notifservice.redis.DuplicateEventSkipService;
import com.notifservice.websocket.NotificationBroadcaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ObjectMapper objectMapper;
    private final DuplicateEventSkipService duplicateEventSkipService;
    private final NotificationBroadcaster broadcaster;

    @KafkaListener(
            topics = "${app.kafka.topics.notifications}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String payload) {
        try {
            Notification notification = objectMapper.readValue(payload, Notification.class);

            duplicateEventSkipService.isNewEvent(notification.getId())
                    .filter(isNew -> isNew)
                    .doOnNext(isNew -> {
                        log.debug("got message from kafka, id={}", notification.getId());
                        broadcaster.broadcast(notification);
                    })
                    .subscribe();

        } catch (Exception e) {
            log.error("could not process message: {}", e.getMessage());
        }
    }
}