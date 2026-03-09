package com.notifservice.websocket;

import com.notifservice.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC = "/topic/notifications";

    public void broadcast(Notification notification) {
        log.info("pushing to ws: {}", notification.getId());
        messagingTemplate.convertAndSend(TOPIC, notification);
    }
}