package com.damian.whatsapp.group.http;

public class RoomMessage {
    public Long roomId;
    public Long senderId;
    public String sender;
    public String message;
    public RoomMessageType type;

    public enum RoomMessageType {
        MESSAGE,
        JOIN_ROOM,
        EXIT_ROOM,
        KICK_USER,
        MUTE_USER
    }
}
