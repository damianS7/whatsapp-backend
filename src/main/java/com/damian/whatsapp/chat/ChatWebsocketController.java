package com.damian.whatsapp.chat;

import com.damian.whatsapp.group.GroupService;
import com.damian.whatsapp.group.http.RoomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class ChatWebsocketController {
    private final GroupService groupService;

    @Autowired
    public ChatWebsocketController(GroupService groupService) {
        this.groupService = groupService;
    }

    @MessageMapping("/rooms/{id}")
    @SendTo("/rooms/{id}")
    public RoomMessage sendToRoom(
            @DestinationVariable Long id,
            RoomMessage message
    ) {

        return message;
    }
}

