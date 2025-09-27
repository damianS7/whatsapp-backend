package com.damian.whatsapp.modules.contact.exception;


public class ContactNotFoundException extends ContactException {
    public ContactNotFoundException(String message, Long userId, Long contactUserId) {
        super(message, userId, contactUserId);
    }
}
