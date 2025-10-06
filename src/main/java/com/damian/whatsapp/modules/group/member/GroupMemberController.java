package com.damian.whatsapp.modules.group.member;

import com.damian.whatsapp.modules.group.group.dto.request.GroupMemberUpdateRequest;
import com.damian.whatsapp.modules.group.group.dto.response.GroupMemberDto;
import com.damian.whatsapp.modules.group.member.dto.mapper.GroupMemberDtoMapper;
import com.damian.whatsapp.shared.domain.GroupMember;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class GroupMemberController {
    private final GroupMemberService groupMemberService;

    @Autowired
    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    // endpoint to fetch all members from a group
    @GetMapping("/groups/{id}/members")
    public ResponseEntity<?> getGroupMembers(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        Set<GroupMember> groups = groupMemberService.getGroupMembers(id);
        Set<GroupMemberDto> groupsDTO = GroupMemberDtoMapper.toGroupMemberDTOList(groups);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupsDTO);
    }

    // endpoint to update groups
    @PostMapping("/groups/{id}/members")
    public ResponseEntity<?> addMember(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            GroupMemberUpdateRequest request
    ) {
        GroupMember groupMember = groupMemberService.addGroupMember(id, request);
        GroupMemberDto groupDTO = GroupMemberDtoMapper.toGroupMemberDTO(groupMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupDTO);
    }

    // endpoint to delete group members
    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public ResponseEntity<?> deleteMember(
            @PathVariable @NotNull @Positive
            Long groupId,
            @PathVariable @NotNull @Positive
            Long userId
    ) {
        groupMemberService.removeGroupMember(groupId, userId);

        return ResponseEntity
                .noContent()
                .build();
    }
}

