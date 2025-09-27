package com.damian.whatsapp.modules.contact.exception;


public class MaxContactsLimitReachedException extends ContactException {
    public MaxContactsLimitReachedException(String message, Long userId, Long contactUserId) {
        super(message, userId, contactUserId);
    }
}
