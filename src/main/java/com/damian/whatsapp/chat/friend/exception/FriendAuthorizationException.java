package com.damian.whatsapp.chat.friend.exception;

import com.damian.whatsapp.auth.exception.AuthorizationException;

public class FriendAuthorizationException extends AuthorizationException {
    public FriendAuthorizationException(String message) {
        super(message);
    }
}
