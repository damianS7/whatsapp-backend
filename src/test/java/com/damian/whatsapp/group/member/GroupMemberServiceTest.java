package com.damian.whatsapp.group.member;

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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupMemberServiceTest {

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
}
