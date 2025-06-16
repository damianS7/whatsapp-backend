package com.damian.whatsapp.contact.exception;

import com.damian.whatsapp.auth.exception.AuthorizationException;

public class ContactAuthorizationException extends AuthorizationException {
    public ContactAuthorizationException(String message) {
        super(message);
    }
}
