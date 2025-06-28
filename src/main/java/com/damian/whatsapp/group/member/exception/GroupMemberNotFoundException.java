package com.damian.whatsapp.group.member.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class GroupMemberNotFoundException extends ApplicationException {
    public GroupMemberNotFoundException(String message) {
        super(message);
    }
}
