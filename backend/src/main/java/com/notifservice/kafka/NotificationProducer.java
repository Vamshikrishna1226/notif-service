package com.notifservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifservice.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.notifications}")
    private String notificationsTopic;

    public void publish(Notification notification) {
        try {
            String payload = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send(notificationsTopic, notification.getId(), payload);
            log.info("sent to kafka [{}] type={}", notification.getId(), notification.getType());
        } catch (JsonProcessingException e) {
            log.error("serialization failed: {}", e.getMessage());
        }
    }
}