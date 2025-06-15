package com.damian.whatsapp.auth.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message) {
        super(message);
    }
}
