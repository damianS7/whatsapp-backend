package com.damian.words.chat.friend;

import com.damian.words.chat.friend.exception.FriendAuthorizationException;
import com.damian.words.chat.friend.exception.MaxFriendsLimitReachedException;
import com.damian.words.common.exception.Exceptions;
import com.damian.words.common.utils.AuthHelper;
import com.damian.words.customer.Customer;
import com.damian.words.customer.CustomerRepository;
import com.damian.words.customer.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FriendService {
    private final short MAX_FRIENDS = 10;
    private final FriendRepository friendRepository;
    private final CustomerRepository customerRepository;

    public FriendService(
            FriendRepository friendRepository,
            CustomerRepository customerRepository
    ) {
        this.friendRepository = friendRepository;
        this.customerRepository = customerRepository;
    }

    // get all the friends for the logged customer
    public Set<Friend> getFriends() {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        return friendRepository.findAllByCustomerId(loggedCustomer.getId());
    }

    // add a new friend for the logged customer
    public Friend addFriend(Long friendCustomerId) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        // check friendlist size limit
        if (this.getFriends().size() >= MAX_FRIENDS) {
            throw new MaxFriendsLimitReachedException(Exceptions.FRIENDLIST.MAX_FRIENDS);
        }

        // check if the customer we are trying to add as a friend exists
        Customer friend = customerRepository.findById(friendCustomerId).orElseThrow(
                () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
        );

        return friendRepository.save(
                new Friend(loggedCustomer, friend)
        );
    }

    // delete a friend from the friendlist of the logged customer
    public void deleteFriend(Long id) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        // check if the friendship exists
        Friend friend = friendRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
        );

        // check if the logged customer is the owner of the friendship
        if (!loggedCustomer.getId().equals(friend.getCustomer().getId())) {
            throw new FriendAuthorizationException(Exceptions.FRIENDLIST.ACCESS_FORBIDDEN);
        }

        friendRepository.deleteById(id);
    }
}
