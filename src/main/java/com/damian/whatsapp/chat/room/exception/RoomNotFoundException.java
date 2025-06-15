package com.damian.whatsapp.chat.room.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class RoomNotFoundException extends ApplicationException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
