package com.damian.whatsapp.modules.chat.service;

import com.damian.whatsapp.chat.http.ChatMessage;
import com.damian.whatsapp.shared.domain.Group;
import com.damian.whatsapp.shared.domain.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class ChatNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public ChatNotificationService(
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyCustomer(
            Long groupId,
            User toUser,
            String message
    ) {
        // send notification to the added member
        ChatMessage chatMessage = new ChatMessage(
                "GROUP" + groupId,
                groupId,
                -1L,
                toUser.getId(),
                "SYSTEM",
                "GROUP",
                message,
                Instant.now()
        );
        messagingTemplate.convertAndSend(
                "/topic/chat.PRIVATE" + toUser.getId(),
                chatMessage
        );
    }

    public void notifyGroup(Group group, String message) {
        // send notification to the added member
        ChatMessage chatMessage = new ChatMessage(
                "GROUP" + group.getId(),
                group.getId(),
                -1L,
                -1L,
                "SYSTEM",
                "GROUP",
                message,
                Instant.now()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat.GROUP" + group.getId(),
                chatMessage
        );
    }
}
