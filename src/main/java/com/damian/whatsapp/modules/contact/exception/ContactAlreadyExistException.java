package com.damian.whatsapp.modules.contact.exception;


public class ContactAlreadyExistException extends ContactException {
    public ContactAlreadyExistException(String message, Long userId, Long contactUserId) {
        super(message, userId, contactUserId);
    }
}
