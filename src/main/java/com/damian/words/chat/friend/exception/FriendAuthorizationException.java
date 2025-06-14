package com.damian.words.chat.friend.exception;

import com.damian.words.auth.exception.AuthorizationException;

public class FriendAuthorizationException extends AuthorizationException {
    public FriendAuthorizationException(String message) {
        super(message);
    }
}
