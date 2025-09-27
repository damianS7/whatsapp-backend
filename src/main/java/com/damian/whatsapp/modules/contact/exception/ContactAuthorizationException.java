package com.damian.whatsapp.modules.contact.exception;

public class ContactAuthorizationException extends ContactException {
    public ContactAuthorizationException(String message, Long userId, Long contactUserId) {
        super(message, userId, contactUserId);
    }
}
