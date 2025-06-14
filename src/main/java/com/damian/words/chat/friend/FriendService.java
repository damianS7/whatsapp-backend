package com.damian.words.chat.friend;

import com.damian.words.common.exception.Exceptions;
import com.damian.words.common.utils.AuthHelper;
import com.damian.words.customer.Customer;
import com.damian.words.customer.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final CustomerRepository customerRepository;

    public FriendService(
            FriendRepository friendRepository,
            CustomerRepository customerRepository
    ) {
        this.friendRepository = friendRepository;
        this.customerRepository = customerRepository;
    }

    public Set<Friend> getContacts() {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        return friendRepository.findAllByCustomerId(loggedCustomer.getId());
    }

    public Friend addContact(Long customerContactId) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        Customer contact = customerRepository.findById(customerContactId).orElseThrow(
                () -> new RuntimeException(Exceptions.CUSTOMER.NOT_FOUND)
        );

        Friend friend = new Friend(loggedCustomer, contact);
        return friendRepository.save(friend);
    }

    public void deleteContact(Long id) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        Friend friend = friendRepository.findById(id).orElseThrow(
                () -> new RuntimeException(Exceptions.CONTACT.NOT_FOUND)
        );

        if (!loggedCustomer.getId().equals(friend.getCustomer().getId())) {
            throw new RuntimeException(Exceptions.CONTACT.ACCESS_FORBIDDEN);
        }

        friendRepository.deleteById(id);
    }
}
