package com.damian.whatsapp.modules.group.service;

import com.damian.whatsapp.modules.chat.ChatNotificationService;
import com.damian.whatsapp.modules.group.exception.GroupAuthorizationException;
import com.damian.whatsapp.modules.group.exception.GroupMemberNotFoundException;
import com.damian.whatsapp.modules.group.exception.GroupNotFoundException;
import com.damian.whatsapp.modules.group.repository.GroupMemberRepository;
import com.damian.whatsapp.modules.group.repository.GroupRepository;
import com.damian.whatsapp.modules.group.web.rest.dto.request.GroupMemberUpdateRequest;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.Group;
import com.damian.whatsapp.shared.domain.GroupMember;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class GroupMemberService {
    private final ChatNotificationService chatNotificationService;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final UserRepository customerRepository;

    public GroupMemberService(
            ChatNotificationService chatNotificationService,
            GroupMemberRepository groupMemberRepository,
            GroupRepository groupRepository,
            UserRepository customerRepository
    ) {
        this.chatNotificationService = chatNotificationService;
        this.groupMemberRepository = groupMemberRepository;
        this.groupRepository = groupRepository;
        this.customerRepository = customerRepository;
    }

    public Set<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }

    public GroupMember addGroupMember(Long groupId, GroupMemberUpdateRequest request) {
        User loggedUser = AuthHelper.getLoggedUser();

        User customer = customerRepository.findById(request.memberId()).orElseThrow(
                () -> new UserNotFoundException(Exceptions.USER.NOT_FOUND, request.memberId())
        );

        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND, groupId)
        );

        GroupMember groupMember = new GroupMember(
                customer,
                group
        );

        // send notification to the group
        chatNotificationService.notifyGroup(
                group,
                loggedUser.getFullName() + " added " + customer.getFullName() + " to the group!"
        );

        return groupMemberRepository.save(groupMember);
    }

    public void removeGroupMember(Long groupMemberId) {
        User loggedUser = AuthHelper.getLoggedUser();

        GroupMember groupMember = groupMemberRepository.findById(groupMemberId).orElseThrow(
                () -> new GroupMemberNotFoundException(Exceptions.GROUP.NOT_FOUND, null, groupMemberId)
        );

        Group group = groupRepository.findById(groupMember.getGroup().getId()).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND, groupMember.getGroup().getId())
        );

        // check authorization
        if (!loggedUser.getId().equals(group.getOwner().getId())) {
            throw new GroupAuthorizationException(Exceptions.GROUP.ACCESS_FORBIDDEN, group.getId());
        }

        groupMemberRepository.deleteById(groupMemberId);

        // send notification to the group
        chatNotificationService.notifyGroup(
                group,
                loggedUser.getFullName() + " removed " + groupMember.getMember().getFullName() + " from the group!"
        );
    }
}
