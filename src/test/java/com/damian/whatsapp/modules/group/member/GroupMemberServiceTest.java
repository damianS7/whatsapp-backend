package com.damian.whatsapp.modules.group.member;

import com.damian.whatsapp.modules.chat.service.ChatNotificationService;
import com.damian.whatsapp.modules.group.group.dto.request.GroupMemberUpdateRequest;
import com.damian.whatsapp.modules.group.group.repository.GroupRepository;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.Group;
import com.damian.whatsapp.shared.domain.GroupMember;
import com.damian.whatsapp.shared.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupMemberServiceTest extends AbstractServiceTest {

    @Mock
    private ChatNotificationService chatNotificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private GroupMemberService groupMemberService;

    @Test
    @DisplayName("Should get all groups that user belongs to")
    void shouldGetAllGroupMembers() {
        // given
        User user = new User(
                1L,
                "user@test.com",
                passwordEncoder.encode("123456")
        );
        //        setUpContext(user);

        Group group = new Group("Gaming", "Gaming description");
        Set<GroupMember> groupMembers = Set.of(
                new GroupMember(user, group)
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
        User user = new User(
                1L,
                "user@test.com",
                passwordEncoder.encode("123456")
        );
        setUpContext(user);

        Group group = new Group(
                "Gaming",
                "Gaming group"
        );
        group.setOwner(user);
        group.setId(1L);

        User userMember = new User(
                2L,
                "userMember@test.com",
                passwordEncoder.encode("123456")
        );

        GroupMemberUpdateRequest request = new GroupMemberUpdateRequest(
                userMember.getId()
        );

        // when
        when(userRepository.findById(userMember.getId())).thenReturn(Optional.of(userMember));
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
        User user = new User(
                1L,
                "user@test.com",
                passwordEncoder.encode("123456")
        );
        setUpContext(user);

        Group group = new Group(
                "Gaming",
                "Gaming group"
        );
        group.setOwner(user);
        group.setId(1L);

        User userMember = new User(
                2L,
                "userMember@test.com",
                passwordEncoder.encode("123456")
        );

        GroupMember groupMember = new GroupMember(userMember, group);
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
