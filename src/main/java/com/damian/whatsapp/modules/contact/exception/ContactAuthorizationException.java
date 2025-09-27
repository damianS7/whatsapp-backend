package com.damian.whatsapp.modules.contact.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class ContactAuthorizationException extends ApplicationException {
    public ContactAuthorizationException(String message) {
        super(message);
    }
}
