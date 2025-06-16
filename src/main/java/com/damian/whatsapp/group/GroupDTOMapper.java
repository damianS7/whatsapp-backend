package com.damian.whatsapp.group;

import com.damian.whatsapp.group.member.GroupMemberDTOMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupDTOMapper {
    public static GroupDTO toGroupDTO(Group group) {
        return new GroupDTO(
                group.getId(),
                group.getName(),
                group.getDescription(),
                GroupMemberDTOMapper.toGroupMemberDTOList(group.getMembers())
        );
    }

    public static Set<GroupDTO> toGroupDTOList(Set<Group> groups) {
        return groups
                .stream()
                .map(
                        GroupDTOMapper::toGroupDTO
                ).collect(Collectors.toSet());
    }
}
