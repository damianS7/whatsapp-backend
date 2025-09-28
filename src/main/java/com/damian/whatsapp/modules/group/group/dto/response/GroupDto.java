package com.damian.whatsapp.modules.group.group.dto.response;

import java.util.Set;

public record GroupDto(
        Long id,
        String name,
        String description,
        GroupUserOwnerDto owner,
        Set<GroupMemberDto> members
) {
}
