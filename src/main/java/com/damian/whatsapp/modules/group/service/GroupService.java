package com.damian.whatsapp.modules.group.service;

import com.damian.whatsapp.modules.group.exception.GroupAuthorizationException;
import com.damian.whatsapp.modules.group.exception.GroupNotFoundException;
import com.damian.whatsapp.modules.group.repository.GroupRepository;
import com.damian.whatsapp.modules.group.web.rest.dto.request.GroupCreateRequest;
import com.damian.whatsapp.modules.group.web.rest.dto.request.GroupUpdateRequest;
import com.damian.whatsapp.shared.domain.Group;
import com.damian.whatsapp.shared.domain.GroupMember;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(
            GroupRepository groupRepository
    ) {
        this.groupRepository = groupRepository;
    }

    public Set<Group> getGroups() {
        User loggedUser = AuthHelper.getLoggedUser();
        return groupRepository.findBelongingGroupsByUserId(loggedUser.getId());
    }

    public Group getGroup(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND, groupId)
        );
    }

    public Group createGroup(GroupCreateRequest request) {
        User loggedUser = AuthHelper.getLoggedUser();
        Group group = new Group(
                request.name(),
                request.description()
        );
        group.setOwner(loggedUser);

        // add the logged customer as member
        GroupMember groupMember = new GroupMember(
                loggedUser,
                group
        );
        group.addMember(groupMember);

        return groupRepository.save(group);
    }

    public Group updateGroup(Long groupId, GroupUpdateRequest request) {
        User loggedUser = AuthHelper.getLoggedUser();
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND, groupId)
        );

        // check if the logged customer is the owner of the group.
        if (!loggedUser.getId().equals(group.getOwner().getId())) {
            throw new GroupAuthorizationException(Exceptions.GROUP.ACCESS_FORBIDDEN, groupId);
        }

        group.setName(request.name());
        group.setDescription(request.description());

        return groupRepository.save(group);
    }

    public void deleteGroup(Long groupId) {
        User loggedUser = AuthHelper.getLoggedUser();

        // check if the group exists
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND, groupId)
        );

        // check if the customer is the owner of the group
        if (!group.getOwner().getId().equals(loggedUser.getId())) {
            throw new GroupAuthorizationException(Exceptions.GROUP.ACCESS_FORBIDDEN, groupId);
        }

        groupRepository.deleteById(groupId);
    }
}
