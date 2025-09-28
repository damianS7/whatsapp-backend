package com.damian.whatsapp.modules.group.group;

import com.damian.whatsapp.modules.chat.service.ChatNotificationService;
import com.damian.whatsapp.modules.group.group.dto.request.GroupCreateRequest;
import com.damian.whatsapp.modules.group.group.dto.request.GroupUpdateRequest;
import com.damian.whatsapp.modules.group.group.exception.GroupAuthorizationException;
import com.damian.whatsapp.modules.group.group.exception.GroupNotFoundException;
import com.damian.whatsapp.modules.group.group.repository.GroupRepository;
import com.damian.whatsapp.modules.group.group.service.GroupService;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.Group;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupServiceTest extends AbstractServiceTest {

    @Mock
    private ChatNotificationService chatNotificationService;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    @DisplayName("Should get all groups that user belongs to")
    void shouldGetAllGroups() {
        // given
        User user = new User(1L, "user@test.com", passwordEncoder.encode("123456"));
        setUpContext(user);

        Group group1 = new Group("gaming", "room1");
        Group group2 = new Group("music", "room2");

        Set<Group> groupList = Set.of(
                group1, group2
        );

        // when
        when(groupRepository.findBelongingGroupsByUserId(user.getId())).thenReturn(groupList);
        Set<Group> result = groupService.getGroups();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(groupRepository, times(1)).findBelongingGroupsByUserId(user.getId());
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
        User user = new User(1L, "user@test.com", passwordEncoder.encode("123456"));
        setUpContext(user);

        GroupCreateRequest request = new GroupCreateRequest(
                "Gaming",
                "Gaming group"
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

    @Test
    @DisplayName("Should update group")
    void shouldUpdateGroup() {
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

        GroupUpdateRequest request = new GroupUpdateRequest(
                "Gaming Streams.",
                "Gaming streams group"
        );

        // when
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupRepository.save(group)).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        Group result = groupService.updateGroup(group.getId(), request);

        // then
        assertNotNull(result);
        assertEquals(request.name(), result.getName());
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    @DisplayName("Should delete group")
    void shouldDeleteGroup() {
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

        // when
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        groupService.deleteGroup(group.getId());

        // then
        verify(groupRepository, times(1)).deleteById(group.getId());
    }

    @Test
    @DisplayName("Should not delete group when not found")
    void shouldNotDeleteGroupWhenNotFound() {
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

        // when
        when(groupRepository.findById(group.getId())).thenReturn(Optional.empty());
        GroupNotFoundException exception = assertThrows(
                GroupNotFoundException.class,
                () -> groupService.deleteGroup(group.getId())
        );

        // then
        assertEquals(Exceptions.GROUP.NOT_FOUND, exception.getMessage());
        verify(groupRepository, times(0)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should not delete group when not owner")
    void shouldNotDeleteGroupWhenNotOwner() {
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
        group.setOwner(new User(2L, "user2@test.com", passwordEncoder.encode("123456")));
        group.setId(1L);

        // when
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        GroupAuthorizationException exception = assertThrows(
                GroupAuthorizationException.class,
                () -> groupService.deleteGroup(group.getId())
        );

        // then
        assertEquals(Exceptions.GROUP.ACCESS_FORBIDDEN, exception.getMessage());
        verify(groupRepository, times(0)).deleteById(anyLong());
    }
}
