package com.damian.whatsapp.contact.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class MaxContactsLimitReachedException extends ApplicationException {
    public MaxContactsLimitReachedException(String message) {
        super(message);
    }
}
