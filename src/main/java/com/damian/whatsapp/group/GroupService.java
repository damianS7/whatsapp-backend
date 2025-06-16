package com.damian.whatsapp.group;

import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.group.exception.GroupNotFoundException;
import com.damian.whatsapp.group.http.GroupCreateRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(
            GroupRepository groupRepository
    ) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getRooms() {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        return groupRepository.findGroupsByCustomerId(loggedCustomer.getId());
    }

    public Group getGroup(Long id) {
        return groupRepository.findById(id).orElseThrow(
                () -> new GroupNotFoundException(Exceptions.GROUP.NOT_FOUND)
        );
    }

    public Group createGroup(GroupCreateRequest request) {
        Group group = new Group(
                request.name(),
                request.description()
        );
        return groupRepository.save(group);
    }
}
