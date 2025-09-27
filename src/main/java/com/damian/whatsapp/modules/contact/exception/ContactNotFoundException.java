package com.damian.whatsapp.modules.contact.exception;


import com.damian.whatsapp.shared.exception.ApplicationException;

public class ContactNotFoundException extends ApplicationException {
    public ContactNotFoundException(String message) {
        super(message);
    }
}
