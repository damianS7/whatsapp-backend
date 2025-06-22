package com.damian.whatsapp.group.dto;

import com.damian.whatsapp.group.member.GroupMemberDTO;

import java.util.Set;

public record GroupDTO(
        Long id,
        String name,
        String description,
        GroupCustomerOwnerDTO owner,
        Set<GroupMemberDTO> members
) {
}
