package com.damian.whatsapp.group;

import com.damian.whatsapp.group.member.GroupMemberDTO;

import java.util.Set;

public record GroupDTO(
        Long id,
        String name,
        String description,
        Set<GroupMemberDTO> members
) {
}
