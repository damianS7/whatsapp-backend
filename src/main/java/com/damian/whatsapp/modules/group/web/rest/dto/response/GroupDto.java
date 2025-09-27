package com.damian.whatsapp.modules.group.web.rest.dto.response;

import java.util.Set;

public record GroupDto(
        Long id,
        String name,
        String description,
        GroupCustomerOwnerDto owner,
        Set<GroupMemberDto> members
) {
}
