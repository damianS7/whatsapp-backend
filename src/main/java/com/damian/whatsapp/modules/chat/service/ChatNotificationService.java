package com.damian.whatsapp.modules.chat.service;

import com.damian.whatsapp.modules.chat.ChatType;
import com.damian.whatsapp.modules.chat.dto.ChatMessageResponse;
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

    public void notifyUser(
            Long groupId,
            User toUser,
            String message
    ) {
        // send notification to the added member
        ChatMessageResponse response = new ChatMessageResponse(
                ChatType.GROUP,
                groupId,
                toUser.getId(),
                "SYSTEM",
                message,
                Instant.now()
        );
        messagingTemplate.convertAndSendToUser(toUser.getEmail(), "/queue/messages", response);
    }

    public void notifyGroup(Group group, String message) {
        // send notification to the added member
        ChatMessageResponse chatMessage = new ChatMessageResponse(
                ChatType.GROUP,
                group.getId(),
                0L,
                "SYSTEM",
                message,
                Instant.now()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + ChatType.GROUP + "/" + group.getId(),
                chatMessage
        );
    }
}
