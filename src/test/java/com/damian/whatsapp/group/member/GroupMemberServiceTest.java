package com.damian.whatsapp.group.member;

import com.damian.whatsapp.chat.ChatNotificationService;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.group.Group;
import com.damian.whatsapp.group.GroupRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupMemberServiceTest {

    @Mock
    private ChatNotificationService chatNotificationService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private GroupMemberService groupMemberService;

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
    void shouldGetAllGroupMembers() {
        // given
        Customer customer = new Customer(
                1L,
                "customer@test.com",
                passwordEncoder.encode("123456")
        );
        //        setUpContext(customer);

        Group group = new Group("Gaming", "Gaming description");
        Set<GroupMember> groupMembers = Set.of(
                new GroupMember(customer, group)
        );
        group.setMembers(
                groupMembers
        );

        // when
        when(groupMemberService.getGroupMembers(group.getId())).thenReturn(groupMembers);
        Set<GroupMember> result = groupMemberService.getGroupMembers(group.getId());

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(groupMemberRepository, times(1)).findByGroupId(group.getId());
    }

    @Test
    @DisplayName("Should add group member")
    void shouldAddGroupMember() {
        // given
        Customer customer = new Customer(
                1L,
                "customer@test.com",
                passwordEncoder.encode("123456")
        );
        setUpContext(customer);

        Group group = new Group(
                "Gaming",
                "Gaming group"
        );
        group.setOwner(customer);
        group.setId(1L);

        Customer customerMember = new Customer(
                2L,
                "customerMember@test.com",
                passwordEncoder.encode("123456")
        );

        GroupMemberUpdateRequest request = new GroupMemberUpdateRequest(
                customerMember.getId()
        );

        // when
        when(customerRepository.findById(customerMember.getId())).thenReturn(Optional.of(customerMember));
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        doNothing().when(chatNotificationService).notifyGroup(any(Group.class), anyString());
        when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        GroupMember result = groupMemberService.addGroupMember(group.getId(), request);

        // then
        assertNotNull(result);
        verify(groupMemberRepository, times(1)).save(any(GroupMember.class));
    }

    @Test
    @DisplayName("Should delete group member")
    void shouldDeleteGroupMember() {
        // given
        Customer customer = new Customer(
                1L,
                "customer@test.com",
                passwordEncoder.encode("123456")
        );
        setUpContext(customer);

        Group group = new Group(
                "Gaming",
                "Gaming group"
        );
        group.setOwner(customer);
        group.setId(1L);

        Customer customerMember = new Customer(
                2L,
                "customerMember@test.com",
                passwordEncoder.encode("123456")
        );

        GroupMember groupMember = new GroupMember(customerMember, group);
        groupMember.setId(1L);

        // when
        when(groupMemberRepository.findById(groupMember.getId())).thenReturn(Optional.of(groupMember));
        when(groupRepository.findById(groupMember.getGroup().getId())).thenReturn(Optional.of(group));
        doNothing().when(groupMemberRepository).deleteById(groupMember.getId());
        groupMemberService.removeGroupMember(groupMember.getId());

        // then
        verify(groupMemberRepository, times(1)).deleteById(groupMember.getId());
    }
}
