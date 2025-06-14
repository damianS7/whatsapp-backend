package com.damian.words.chat.friend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class FriendController {
    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    // endpoint to receive logged customer
    @GetMapping("/friends")
    public ResponseEntity<?> getContacts() {
        Set<Friend> friends = friendService.getFriends();
        Set<FriendDTO> contactsDTO = FriendDTOMapper.toCustomerContactDTOList(friends);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactsDTO);
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<?> addContact(
            @PathVariable @NotBlank @Positive
            Long id
    ) {
        Friend contact = friendService.addFriend(id);
        FriendDTO contactDTO = FriendDTOMapper.toCustomerContactDTO(contact);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactDTO);
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<?> deleteContact(
            @PathVariable @NotBlank @Positive
            Long id
    ) {
        friendService.deleteFriend(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

