package com.damian.whatsapp.group;

import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.group.exception.GroupNotFoundException;
import com.damian.whatsapp.group.http.GroupCreateRequest;
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

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private GroupService groupService;

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
    @DisplayName("Should get all groups that customer belongs to")
    void shouldGetAllGroups() {
        // given
        Customer customer = new Customer(1L, "customer@test.com", passwordEncoder.encode("123456"));
        setUpContext(customer);

        Group group1 = new Group("gaming", "room1");
        Group group2 = new Group("music", "room2");

        Set<Group> groupList = Set.of(
                group1, group2
        );

        // when
        when(groupRepository.findGroupsByOwnerCustomerId(customer.getId())).thenReturn(groupList);
        Set<Group> result = groupService.getGroups();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(groupRepository, times(1)).findGroupsByOwnerCustomerId(customer.getId());
    }

    @Test
    @DisplayName("Should get group")
    void shouldGetGroup() {
        // given
        Group group1 = new Group("gaming", "group1");
        group1.setId(1L);

        // when
        when(groupRepository.findById(group1.getId())).thenReturn(Optional.of(group1));
        Group result = groupService.getGroup(group1.getId());

        // then
        assertNotNull(result);
        verify(groupRepository, times(1)).findById(group1.getId());
    }

    @Test
    @DisplayName("Should not get group")
    void shouldNotGetGroupWhenNotFound() {
        // given
        Group group1 = new Group("gaming", "group1");
        group1.setId(1L);

        // when
        when(groupRepository.findById(group1.getId())).thenReturn(Optional.empty());
        GroupNotFoundException exception = assertThrows(
                GroupNotFoundException.class,
                () -> groupService.getGroup(group1.getId())
        );

        // then
        assertEquals(Exceptions.GROUP.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should create group")
    void shouldCreateGroup() {
        // given
        Customer customer = new Customer(1L, "customer@test.com", passwordEncoder.encode("123456"));
        setUpContext(customer);

        GroupCreateRequest request = new GroupCreateRequest(
                "Gaming",
                "Gaming group",
                Set.of()
        );

        // when
        when(groupRepository.save(any(Group.class))).thenReturn(
                new Group(request.name(), request.description())
        );
        Group result = groupService.createGroup(request);

        // then
        assertNotNull(result);
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    // TODO: shouldDeleteRoom
}
