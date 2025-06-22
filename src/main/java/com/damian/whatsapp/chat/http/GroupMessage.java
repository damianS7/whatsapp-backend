package com.damian.whatsapp.chat.http;

import java.time.LocalDate;

public class GroupMessage {
    public Long roomId;
    public Long senderId;
    public String sender;
    public String message;
    public LocalDate timestamp;
    public RoomMessageType type;

    public enum RoomMessageType {
        MESSAGE,
        JOIN_ROOM,
        EXIT_ROOM,
        KICK_USER,
        MUTE_USER
    }
}
