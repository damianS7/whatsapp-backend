package com.damian.whatsapp.modules.contact.exception;


import com.damian.whatsapp.shared.exception.ApplicationException;

public class ContactAlreadyExistException extends ApplicationException {
    public ContactAlreadyExistException(String message) {
        super(message);
    }
}
