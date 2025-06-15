package com.damian.whatsapp.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class ChatController {

    @Autowired
    public ChatController() {
    }


    // Endpoint al cual enviar los mensajes
    //    @MessageMapping("/chat")
    // Canal de susbscripcion, todos aquellos suscritos a /room/message
    // recibiran los mensajes por medio de broadcast
    //    @SendTo("/room/message")
    //    public ConversationMessage send(@RequestBody RoomMessageRequest request) {
    //        return new RoomMessageResponse(request.roomId, request.senderId, request.sender, request.message);
    //        return null;
    //    }
}

