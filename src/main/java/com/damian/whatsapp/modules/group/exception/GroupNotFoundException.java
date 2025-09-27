package com.damian.whatsapp.modules.group.exception;


public class GroupNotFoundException extends GroupException {
    public GroupNotFoundException(String message, Long groupId) {
        super(message, groupId);
    }
}
