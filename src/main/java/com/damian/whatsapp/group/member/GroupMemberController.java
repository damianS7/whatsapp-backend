package com.damian.whatsapp.group.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/groups/{id}")
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
}

