package com.damian.whatsapp.chat.room;

import com.damian.whatsapp.chat.room.http.RoomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class RoomWebsocketController {
    private final RoomService roomService;

    @Autowired
    public RoomWebsocketController(RoomService roomService) {
        this.roomService = roomService;
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

