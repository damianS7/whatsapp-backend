package com.damian.whatsapp.modules.group.exception;


import com.damian.whatsapp.shared.exception.ApplicationException;

public class GroupAuthorizationException extends ApplicationException {
    public GroupAuthorizationException(String message) {
        super(message);
    }
}
