package com.damian.whatsapp.modules.chat.controller;

import com.damian.whatsapp.modules.chat.dto.ChatMessageRequest;
import com.damian.whatsapp.modules.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(
            ChatService chatService
    ) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat")
    public void handleChatMessages(
            ChatMessageRequest request, Principal principal, MessageHeaders headers
    ) {
        chatService.handle(request, principal);
    }
}

