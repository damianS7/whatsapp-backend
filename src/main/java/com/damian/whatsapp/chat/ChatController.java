package com.damian.whatsapp.chat;

import com.damian.whatsapp.chat.http.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;


    @Autowired
    public ChatController(
            SimpMessagingTemplate messagingTemplate,
            ChatService chatService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    // endpoint to receive and broadcast messages
    @MessageMapping("/chat.send.{chatId}")
    public void broadcastMessage(
            @DestinationVariable String chatId,
            ChatMessage message
    ) {

        ChatMessage newMessage = new ChatMessage(
                message.chatId(),
                message.groupId(),
                message.fromCustomerId(),
                message.toCustomerId(),
                message.fromCustomerName(),
                message.chatType(),
                message.message(),
                Instant.now()
        );

        String destination = "/topic/chat." + chatId;

        // Send to all users in the channel
        messagingTemplate.convertAndSend(destination, newMessage);
    }


}

