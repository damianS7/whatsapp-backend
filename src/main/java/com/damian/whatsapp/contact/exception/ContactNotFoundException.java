package com.damian.whatsapp.contact.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class ContactNotFoundException extends ApplicationException {
    public ContactNotFoundException(String message) {
        super(message);
    }
}
