package com.damian.whatsapp.modules.group.member.dto.mapper;

import com.damian.whatsapp.modules.group.group.dto.response.GroupMemberDto;
import com.damian.whatsapp.shared.domain.GroupMember;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupMemberDtoMapper {
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
                        GroupMemberDtoMapper::toGroupMemberDTO
                ).collect(Collectors.toSet());
    }
}
