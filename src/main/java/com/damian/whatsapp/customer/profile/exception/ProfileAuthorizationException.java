package com.damian.whatsapp.customer.profile.exception;

import com.damian.whatsapp.auth.exception.AuthorizationException;

public class ProfileAuthorizationException extends AuthorizationException {
    public ProfileAuthorizationException(String message) {
        super(message);
    }
}
