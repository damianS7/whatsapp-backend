package com.damian.whatsapp.contact.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class ContactAlreadyExistException extends ApplicationException {
    public ContactAlreadyExistException(String message) {
        super(message);
    }
}
