package com.damian.whatsapp.chat;

import com.damian.whatsapp.chat.http.ChatMessage;
import com.damian.whatsapp.group.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class ChatController {
    private final GroupService groupService;
    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    public ChatController(
            GroupService groupService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.groupService = groupService;
        this.messagingTemplate = messagingTemplate;
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

