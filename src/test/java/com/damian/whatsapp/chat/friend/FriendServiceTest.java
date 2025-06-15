package com.damian.whatsapp.chat.friend;

import com.damian.whatsapp.chat.friend.exception.FriendAuthorizationException;
import com.damian.whatsapp.chat.friend.exception.MaxFriendsLimitReachedException;
import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.exception.CustomerNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private FriendService friendService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        customerRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    void setUpContext(Customer customer) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customer);
    }

    @Test
    @DisplayName("Should get all friends")
    void shouldGetAllFriends() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer contact1 = new Customer(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        Customer contact2 = new Customer(
                3L, "customer2@test.com", passwordEncoder.encode("password")
        );

        Set<Friend> contactList = Set.of(
                new Friend(loggedCustomer, contact1),
                new Friend(loggedCustomer, contact2)
        );

        // when
        when(friendRepository.findAllByCustomerId(loggedCustomer.getId()))
                .thenReturn(contactList);
        Set<Friend> result = friendService.getFriends();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(friendRepository, times(1)).findAllByCustomerId(loggedCustomer.getId());
    }

    @Test
    @DisplayName("Should add a friend")
    void shouldAddFriend() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer friend1 = new Customer(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        Friend givenCC = new Friend(loggedCustomer, friend1);

        // when
        when(customerRepository.findById(friend1.getId())).thenReturn(Optional.of(friend1));
        when(friendRepository.save(any(Friend.class)))
                .thenReturn(givenCC);

        Friend result = friendService.addFriend(friend1.getId());

        // then
        assertNotNull(result);
        verify(friendRepository, times(1)).save(any(Friend.class));
    }

    @Test
    @DisplayName("Should not add a friend when limit reached")
    void shouldNotAddFriendWhenLimitReached() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);
        short MAX_FRIENDS = 3;

        Field field = null;
        try {
            field = FriendService.class.getDeclaredField("MAX_FRIENDS");
            field.setAccessible(true);
            MAX_FRIENDS = (short) field.get(friendService); // null porque es static
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        Set<Friend> friendList = new HashSet<>();
        for (int i = 0; i <= MAX_FRIENDS; i++) {
            friendList.add(new Friend());
        }

        // when
        when(friendRepository.findAllByCustomerId(loggedCustomer.getId())).thenReturn(friendList);
        MaxFriendsLimitReachedException exception = assertThrows(
                MaxFriendsLimitReachedException.class,
                () -> friendService.addFriend(0L)
        );

        // then
        assertEquals(Exceptions.FRIEND_LIST.MAX_FRIENDS, exception.getMessage());
    }

    @Test
    @DisplayName("Should not add a friend when customer not found")
    void shouldNotAddFriendWhenCustomerNotFound() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer friend1 = new Customer(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        // when
        when(customerRepository.findById(friend1.getId())).thenReturn(Optional.empty());
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> friendService.addFriend(friend1.getId())
        );

        // then
        assertEquals(Exceptions.CUSTOMER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should delete a friend")
    void shouldDeleteFriend() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer contact1 = new Customer(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        Friend givenCC = new Friend(loggedCustomer, contact1);
        givenCC.setId(1L);

        // when
        when(friendRepository.findById(givenCC.getId())).thenReturn(Optional.of(givenCC));
        doNothing().when(friendRepository).deleteById(givenCC.getId());

        friendService.deleteFriend(givenCC.getId());

        // then
        verify(friendRepository, times(1)).deleteById(givenCC.getId());
    }

    @Test
    @DisplayName("Should not delete a friend when customer not found")
    void shouldNotDeleteFriendWhenCustomerNotFound() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        // when
        when(friendRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> friendService.deleteFriend(0L)
        );

        // then
        assertEquals(Exceptions.CUSTOMER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not delete a friend when not authorized")
    void shouldNotDeleteFriendWhenNotAuthorized() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Friend givenCC = new Friend(
                new Customer(5L, "customer1@test.com", passwordEncoder.encode("password")),
                new Customer(8L, "customer2@test.com", passwordEncoder.encode("password"))
        );
        givenCC.setId(1L);

        // when
        when(friendRepository.findById(givenCC.getId())).thenReturn(Optional.of(givenCC));
        FriendAuthorizationException exception = assertThrows(
                FriendAuthorizationException.class,
                () -> friendService.deleteFriend(givenCC.getId())
        );

        // then
        assertEquals(Exceptions.FRIEND_LIST.ACCESS_FORBIDDEN, exception.getMessage());
    }

}
