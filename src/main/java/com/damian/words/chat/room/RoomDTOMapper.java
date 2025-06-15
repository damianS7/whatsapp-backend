package com.damian.words.chat.room;

import java.util.Set;
import java.util.stream.Collectors;

public class RoomDTOMapper {
    public static RoomDTO toRoomDTO(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getName(),
                room.getDescription()
        );
    }

    public static Set<RoomDTO> toRoomDTOList(Set<Room> rooms) {
        return rooms
                .stream()
                .map(
                        RoomDTOMapper::toRoomDTO
                ).collect(Collectors.toSet());
    }
}
