package com.damian.whatsapp.group.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class GroupNotFoundException extends ApplicationException {
    public GroupNotFoundException(String message) {
        super(message);
    }
}
