package com.damian.whatsapp.modules.group.web.rest.dto.mapper;

import com.damian.whatsapp.modules.group.web.rest.dto.response.GroupMemberDto;
import com.damian.whatsapp.shared.domain.GroupMember;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupMemberDTOMapper {
    public static GroupMemberDto toGroupMemberDTO(GroupMember groupMember) {
        return new GroupMemberDto(
                groupMember.getId(),
                groupMember.getGroup().getId(),
                groupMember.getMember().getId(),
                groupMember.getMember().getFullName(),
                groupMember.getMember().getImageFilename()
        );
    }

    public static Set<GroupMemberDto> toGroupMemberDTOList(Set<GroupMember> groupMembers) {
        return groupMembers
                .stream()
                .map(
                        GroupMemberDTOMapper::toGroupMemberDTO
                ).collect(Collectors.toSet());
    }
}
