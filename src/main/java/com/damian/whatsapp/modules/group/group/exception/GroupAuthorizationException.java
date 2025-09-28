package com.damian.whatsapp.modules.group.group.exception;


public class GroupAuthorizationException extends GroupException {
    public GroupAuthorizationException(String message, Long groupId) {
        super(message, groupId);
    }
}
