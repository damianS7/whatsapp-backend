package com.damian.whatsapp.auth.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class AuthorizationException extends ApplicationException {
    public AuthorizationException(String message) {
        super(message);
    }
}
