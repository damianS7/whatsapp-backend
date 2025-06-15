package com.damian.words.chat.friend;

import jakarta.validation.constraints.NotNull;
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
        Set<FriendDTO> friendsDTO = FriendDTOMapper.toCustomerFriendDTOList(friends);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(friendsDTO);
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<?> addFriend(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        Friend friend = friendService.addFriend(id);
        FriendDTO friendDTO = FriendDTOMapper.toCustomerFriendDTO(friend);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(friendDTO);
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<?> deleteFriend(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        friendService.deleteFriend(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

