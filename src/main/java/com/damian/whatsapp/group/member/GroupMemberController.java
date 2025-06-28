package com.damian.whatsapp.group.member;

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
        Set<GroupMemberDTO> groupsDTO = GroupMemberDTOMapper.toGroupMemberDTOList(groups);

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
        GroupMemberDTO groupDTO = GroupMemberDTOMapper.toGroupMemberDTO(groupMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupDTO);
    }

    // endpoint to delete group members
    @DeleteMapping("/groups/members/{id}")
    public ResponseEntity<?> deleteMember(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        groupMemberService.removeGroupMember(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

