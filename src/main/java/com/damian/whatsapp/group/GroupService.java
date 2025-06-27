package com.damian.whatsapp.group;

import com.damian.whatsapp.chat.http.ChatMessage;
import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.exception.CustomerNotFoundException;
import com.damian.whatsapp.group.exception.GroupAuthorizationException;
import com.damian.whatsapp.group.exception.GroupNotFoundException;
import com.damian.whatsapp.group.http.GroupCreateRequest;
import com.damian.whatsapp.group.http.GroupUpdateRequest;
import com.damian.whatsapp.group.member.GroupMember;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class GroupService {
    private final SimpMessagingTemplate messagingTemplate;
    private final CustomerRepository customerRepository;
    private final GroupRepository groupRepository;

    public GroupService(
            SimpMessagingTemplate messagingTemplate,
            CustomerRepository customerRepository,
            GroupRepository groupRepository
    ) {
        this.messagingTemplate = messagingTemplate;
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
        group.getMembers().add(groupMember);

        // add group members by default
        request.membersId().forEach((customerId) -> {
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
            );
            group.getMembers().add(
                    new GroupMember(
                            customer,
                            group
                    ));
        });

        return groupRepository.save(group);
    }

    public Group updateGroup(Long id, GroupUpdateRequest request) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );

        if (!loggedCustomer.getId().equals(group.getOwner().getId())) {
            throw new GroupAuthorizationException(Exceptions.GROUP.ACCESS_FORBIDDEN);
        }

        group.setName(request.name());
        group.setDescription(request.description());

        Set<Long> existingMemberIds = group.getMembers().stream()
                                           .map(member -> member.getMember().getId())
                                           .collect(Collectors.toSet());

        // add group members
        for (Long customerId : request.membersId()) {
            // avoid duplicates
            if (existingMemberIds.contains(customerId)) {
                continue;
            }

            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
            );

            group.getMembers().add(
                    new GroupMember(
                            customer,
                            group
                    ));

            // send notification to the added member
            ChatMessage message = new ChatMessage(
                    "GROUP" + group.getId(),
                    group.getId(),
                    loggedCustomer.getId(),
                    customer.getId(),
                    loggedCustomer.getFullName(),
                    "GROUP",
                    loggedCustomer.getFullName() + " added you to the group!",
                    Instant.now()
            );
            messagingTemplate.convertAndSend(
                    "/topic/chat.PRIVATE" + customer.getId(),
                    message
            );
        }

        // TODO notify the group members about the added members

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
