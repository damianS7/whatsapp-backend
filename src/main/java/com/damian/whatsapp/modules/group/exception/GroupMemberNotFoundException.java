package com.damian.whatsapp.modules.group.exception;

public class GroupMemberNotFoundException extends GroupException {
    private final Long groupMemberId;

    public GroupMemberNotFoundException(String message, Long groupId, Long groupMemberId) {
        super(message, groupId);
        this.groupMemberId = groupMemberId;
    }

    public Long getGroupMemberId() {
        return groupMemberId;
    }
}
