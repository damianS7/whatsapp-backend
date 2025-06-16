package com.damian.whatsapp.group.member;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupMemberDTOMapper {
    public static GroupMemberDTO toGroupMemberDTO(GroupMember groupMember) {
        return new GroupMemberDTO(
                groupMember.getId(),
                groupMember.getGroup().getId(),
                groupMember.getMember().getId(),
                groupMember.getMember().getFullName(),
                groupMember.getMember().getProfile().getAvatarFilename()
        );
    }

    public static Set<GroupMemberDTO> toGroupMemberDTOList(Set<GroupMember> groupMembers) {
        return groupMembers
                .stream()
                .map(
                        GroupMemberDTOMapper::toGroupMemberDTO
                ).collect(Collectors.toSet());
    }
}
