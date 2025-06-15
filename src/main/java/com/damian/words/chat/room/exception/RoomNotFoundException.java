package com.damian.words.chat.room.exception;

import com.damian.words.common.exception.ApplicationException;

public class RoomNotFoundException extends ApplicationException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
