package com.damian.whatsapp.modules.contact.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class ContactException extends ApplicationException {
    private final Long userId;
    private final Long contactUserId;

    public ContactException(String message, Long userId, Long contactUserId) {
        super(message);
        this.userId = userId;
        this.contactUserId = contactUserId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getContactUserId() {
        return contactUserId;
    }
}
