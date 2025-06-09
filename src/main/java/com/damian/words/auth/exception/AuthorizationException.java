package com.damian.words.auth.exception;

import com.damian.words.common.exception.ApplicationException;

public class AuthorizationException extends ApplicationException {
    public AuthorizationException(String message) {
        super(message);
    }
}
