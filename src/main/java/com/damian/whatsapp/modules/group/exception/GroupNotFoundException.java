package com.damian.whatsapp.modules.group.exception;


import com.damian.whatsapp.shared.exception.ApplicationException;

public class GroupNotFoundException extends ApplicationException {
    public GroupNotFoundException(String message) {
        super(message);
    }
}
