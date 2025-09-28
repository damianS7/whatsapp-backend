package com.damian.whatsapp.modules.group.group.dto.mapper;

import com.damian.whatsapp.modules.group.group.dto.response.GroupUserOwnerDto;
import com.damian.whatsapp.modules.group.group.dto.response.GroupDto;
import com.damian.whatsapp.shared.domain.Group;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupDtoMapper {
    public static GroupDto toGroupDTO(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                new GroupUserOwnerDto(
                        group.getOwner().getId(),
                        group.getOwner().getFirstName(),
                        group.getOwner().getImageFilename()
                ),
                GroupMemberDtoMapper.toGroupMemberDTOList(group.getMembers())
        );
    }

    public static Set<GroupDto> toGroupDTOList(Set<Group> groups) {
        return groups
                .stream()
                .map(
                        GroupDtoMapper::toGroupDTO
                ).collect(Collectors.toSet());
    }
}
