package com.damian.whatsapp.group;

import com.damian.whatsapp.group.dto.GroupDTO;
import com.damian.whatsapp.group.http.GroupCreateRequest;
import com.damian.whatsapp.group.http.GroupUpdateRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // endpoint to fetch all groups customer belongs
    @GetMapping("/groups")
    public ResponseEntity<?> getGroups() {
        List<Group> groups = groupService.getGroups();
        List<GroupDTO> groupsDTO = groups.stream().map(GroupDTOMapper::toGroupDTO).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupsDTO);
    }

    // endpoint to create groups
    @PostMapping("/groups")
    public ResponseEntity<?> createGroup(
            @Validated @RequestBody
            GroupCreateRequest request
    ) {
        Group group = groupService.createGroup(request);
        GroupDTO groupDTO = GroupDTOMapper.toGroupDTO(group);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupDTO);
    }

    // endpoint to update groups
    @PutMapping("/groups/{id}")
    public ResponseEntity<?> updateGroup(
            @PathVariable @NotNull @Positive
            Long id,
            @Validated @RequestBody
            GroupUpdateRequest request
    ) {
        Group group = groupService.updateGroup(id, request);
        GroupDTO groupDTO = GroupDTOMapper.toGroupDTO(group);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupDTO);
    }

    // endpoint to delete groups
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<?> deleteGroup(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        groupService.deleteGroup(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

