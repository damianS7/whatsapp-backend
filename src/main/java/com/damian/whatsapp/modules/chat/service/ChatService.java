package com.damian.whatsapp.modules.chat.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messagingTemplate = messagingTemplate;
    }
}
