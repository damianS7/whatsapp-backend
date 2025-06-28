package com.damian.whatsapp.group.member;

import com.damian.whatsapp.chat.ChatNotificationService;
import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.exception.CustomerNotFoundException;
import com.damian.whatsapp.group.Group;
import com.damian.whatsapp.group.GroupRepository;
import com.damian.whatsapp.group.exception.GroupAuthorizationException;
import com.damian.whatsapp.group.exception.GroupNotFoundException;
import com.damian.whatsapp.group.member.exception.GroupMemberNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class GroupMemberService {
    private final ChatNotificationService chatNotificationService;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final CustomerRepository customerRepository;

    public GroupMemberService(
            ChatNotificationService chatNotificationService,
            GroupMemberRepository groupMemberRepository,
            GroupRepository groupRepository,
            CustomerRepository customerRepository
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
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        Customer customer = customerRepository.findById(request.memberId()).orElseThrow(
                () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
        );

        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );

        GroupMember groupMember = new GroupMember(
                customer,
                group
        );

        // send notification to the group
        chatNotificationService.notifyGroup(
                group,
                loggedCustomer.getFullName() + " added " + customer.getFullName() + " to the group!"
        );

        // send notification to the added member
        chatNotificationService.notifyCustomer(
                group.getId(),
                loggedCustomer,
                customer,
                loggedCustomer.getFullName() + " added you to the group!"
        );
        return groupMemberRepository.save(groupMember);
    }

    public void removeGroupMember(Long groupMemberId) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        GroupMember groupMember = groupMemberRepository.findById(groupMemberId).orElseThrow(
                () -> new GroupMemberNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );

        Group group = groupRepository.findById(groupMember.getGroup().getId()).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );

        // check authorization
        if (!loggedCustomer.getId().equals(group.getOwner().getId())) {
            throw new GroupAuthorizationException(Exceptions.GROUP.ACCESS_FORBIDDEN);
        }

        groupMemberRepository.deleteById(groupMemberId);
    }
}
