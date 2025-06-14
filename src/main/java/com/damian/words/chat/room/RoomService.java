package com.damian.words.chat.room;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(
            RoomRepository roomRepository
    ) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    public Room getRoom(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public void joinRoom(Long id) {
        // check if banned
        // check room exists
        // check if user is in room
    }

    public String messageToRoom(Long id, String message) {
        // check if banned
        // check room exists
        // check if user is in room
        return "";
    }
}
