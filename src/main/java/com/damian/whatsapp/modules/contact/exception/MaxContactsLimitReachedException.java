package com.damian.whatsapp.modules.contact.exception;


import com.damian.whatsapp.shared.exception.ApplicationException;

public class MaxContactsLimitReachedException extends ApplicationException {
    public MaxContactsLimitReachedException(String message) {
        super(message);
    }
}
