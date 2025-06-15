package com.damian.whatsapp.chat.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    // endpoint to fetch all rooms
    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms() {
        List<Room> rooms = roomService.getRooms();
        List<RoomDTO> roomsDTO = rooms.stream().map(RoomDTOMapper::toRoomDTO).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomsDTO);
    }
}

