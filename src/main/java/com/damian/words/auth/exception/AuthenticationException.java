package com.damian.words.auth.exception;

import com.damian.words.common.exception.ApplicationException;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message) {
        super(message);
    }
}
