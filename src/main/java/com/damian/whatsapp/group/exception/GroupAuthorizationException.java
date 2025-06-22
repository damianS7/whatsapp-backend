package com.damian.whatsapp.group.exception;

import com.damian.whatsapp.auth.exception.AuthorizationException;

public class GroupAuthorizationException extends AuthorizationException {
    public GroupAuthorizationException(String message) {
        super(message);
    }
}
