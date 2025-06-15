package com.damian.whatsapp.chat.friend.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class MaxFriendsLimitReachedException extends ApplicationException {
    public MaxFriendsLimitReachedException(String message) {
        super(message);
    }
}
