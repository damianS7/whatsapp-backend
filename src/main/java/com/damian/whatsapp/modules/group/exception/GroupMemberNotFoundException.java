package com.damian.whatsapp.modules.group.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class GroupMemberNotFoundException extends ApplicationException {
    public GroupMemberNotFoundException(String message) {
        super(message);
    }
}
