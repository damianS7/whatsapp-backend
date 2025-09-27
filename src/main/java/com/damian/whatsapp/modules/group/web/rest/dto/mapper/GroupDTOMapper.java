package com.damian.whatsapp.modules.group.web.rest.dto.mapper;

import com.damian.whatsapp.modules.group.web.rest.dto.response.GroupCustomerOwnerDto;
import com.damian.whatsapp.modules.group.web.rest.dto.response.GroupDto;
import com.damian.whatsapp.shared.domain.Group;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupDTOMapper {
    public static GroupDto toGroupDTO(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                new GroupCustomerOwnerDto(
                        group.getOwner().getId(),
                        group.getOwner().getFirstName(),
                        group.getOwner().getImageFilename()
                ),
                GroupMemberDTOMapper.toGroupMemberDTOList(group.getMembers())
        );
    }

    public static Set<GroupDto> toGroupDTOList(Set<Group> groups) {
        return groups
                .stream()
                .map(
                        GroupDTOMapper::toGroupDTO
                ).collect(Collectors.toSet());
    }
}
