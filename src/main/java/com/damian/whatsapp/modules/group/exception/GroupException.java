package com.damian.whatsapp.modules.group.exception;


import com.damian.whatsapp.shared.exception.ApplicationException;

public class GroupException extends ApplicationException {
    private final Long groupId;

    public GroupException(String message, Long groupId) {
        super(message);
        this.groupId = groupId;
    }

    public Long getGroupId() {
        return groupId;
    }
}
