package com.damian.words.chat.friend;

import java.util.Set;
import java.util.stream.Collectors;

public class FriendDTOMapper {
    public static FriendDTO toCustomerContactDTO(Friend friend) {
        return new FriendDTO(
                friend.getId(),
                friend.getFriend().getFullName(),
                friend.getFriend().getProfile().getAvatarFilename()
        );
    }

    public static Set<FriendDTO> toCustomerContactDTOList(Set<Friend> customers) {
        return customers
                .stream()
                .map(
                        FriendDTOMapper::toCustomerContactDTO
                ).collect(Collectors.toSet());
    }
}
