package com.damian.whatsapp.group;

import com.damian.whatsapp.chat.ChatNotificationService;
import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.group.exception.GroupAuthorizationException;
import com.damian.whatsapp.group.exception.GroupNotFoundException;
import com.damian.whatsapp.group.http.GroupCreateRequest;
import com.damian.whatsapp.group.http.GroupUpdateRequest;
import com.damian.whatsapp.group.member.GroupMember;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class GroupService {
    private final ChatNotificationService chatNotificationService;
    private final CustomerRepository customerRepository;
    private final GroupRepository groupRepository;

    public GroupService(
            ChatNotificationService chatNotificationService,
            CustomerRepository customerRepository,
            GroupRepository groupRepository
    ) {
        this.chatNotificationService = chatNotificationService;
        this.customerRepository = customerRepository;
        this.groupRepository = groupRepository;
    }

    public Set<Group> getGroups() {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        return groupRepository.findBelongingGroupsByCustomerId(loggedCustomer.getId());
    }

    public Group getGroup(Long id) {
        return groupRepository.findById(id).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );
    }

    public Group createGroup(GroupCreateRequest request) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        Group group = new Group(
                request.name(),
                request.description()
        );
        group.setOwner(loggedCustomer);

        // add the logged customer as member
        GroupMember groupMember = new GroupMember(
                loggedCustomer,
                group
        );
        group.addMember(groupMember);

        return groupRepository.save(group);
    }

    public Group updateGroup(Long id, GroupUpdateRequest request) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );

        // check if the logged customer is the owner of the group.
        if (!loggedCustomer.getId().equals(group.getOwner().getId())) {
            throw new GroupAuthorizationException(Exceptions.GROUP.ACCESS_FORBIDDEN);
        }

        group.setName(request.name());
        group.setDescription(request.description());

        return groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        // check if the group exists
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );

        // check if the customer is the owner of the group
        if (!group.getOwner().getId().equals(loggedCustomer.getId())) {
            throw new GroupAuthorizationException(Exceptions.GROUP.ACCESS_FORBIDDEN);
        }

        groupRepository.deleteById(id);
    }
}
