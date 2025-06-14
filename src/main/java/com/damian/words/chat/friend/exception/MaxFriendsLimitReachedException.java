package com.damian.words.chat.friend.exception;

import com.damian.words.common.exception.ApplicationException;

public class MaxFriendsLimitReachedException extends ApplicationException {
    public MaxFriendsLimitReachedException(String message) {
        super(message);
    }
}
