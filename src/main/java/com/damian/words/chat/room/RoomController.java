package com.damian.words.chat.room;

import com.damian.words.chat.room.http.RoomMessage;
import com.damian.words.chat.room.http.RoomMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // endpoint to receive logged customer
    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms() {
        List<Room> rooms = roomService.getRooms();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rooms);
    }

    // endpoint to receive logged customer
    @GetMapping("/rooms/{id}/join")
    public ResponseEntity<?> joinRoom(Long id) {
        List<Room> rooms = roomService.getRooms();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rooms);
    }

    // endpoint to receive logged customer
    @GetMapping("/rooms/{id}/exit")
    public ResponseEntity<?> exitRoom(Long id) {
        List<Room> rooms = roomService.getRooms();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rooms);
    }

    // Endpoint al cual enviar los mensajes
    //    @MessageMapping("/chat")
    // Canal de susbscripcion, todos aquellos suscritos a /room/message
    // recibiran los mensajes por medio de broadcast
    //    @SendTo("/room/message")
    public RoomMessage send(@RequestBody RoomMessageRequest request) {
        //        return new RoomMessageResponse(request.roomId, request.senderId, request.sender, request.message);
        return null;
    }
}

