package com.damian.words.chat.friend;

public record FriendDTO(
        Long id,
        Long friendCustomerId,
        String name,
        String avatarFilename
) {
}
