package com.damian.whatsapp.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getRooms() {
        List<Group> groups = groupService.getRooms();
        List<GroupDTO> roomsDTO = groups.stream().map(GroupDTOMapper::toGroupDTO).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomsDTO);
    }
}

