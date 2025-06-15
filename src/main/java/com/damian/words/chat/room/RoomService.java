package com.damian.words.chat.room;

import com.damian.words.chat.room.exception.RoomNotFoundException;
import com.damian.words.chat.room.http.RoomCreateRequest;
import com.damian.words.common.exception.Exceptions;
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
        return roomRepository.findById(id).orElseThrow(
                () -> new RoomNotFoundException(Exceptions.ROOM.NOT_FOUND)
        );
    }

    public Room createRoom(RoomCreateRequest request) {
        Room room = new Room(
                request.name(),
                request.description()
        );
        return roomRepository.save(room);
    }
}
