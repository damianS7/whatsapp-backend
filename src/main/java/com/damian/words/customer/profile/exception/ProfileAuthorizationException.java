package com.damian.words.customer.profile.exception;

import com.damian.words.auth.exception.AuthorizationException;

public class ProfileAuthorizationException extends AuthorizationException {
    public ProfileAuthorizationException(String message) {
        super(message);
    }
}
