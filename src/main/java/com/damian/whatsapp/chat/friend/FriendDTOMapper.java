package com.damian.whatsapp.chat.friend;

import java.util.Set;
import java.util.stream.Collectors;

public class FriendDTOMapper {
    public static FriendDTO toCustomerFriendDTO(Friend friend) {
        return new FriendDTO(
                friend.getId(),
                friend.getFriend().getId(),
                friend.getFriend().getFullName(),
                friend.getFriend().getProfile().getAvatarFilename()
        );
    }

    public static Set<FriendDTO> toCustomerFriendDTOList(Set<Friend> customers) {
        return customers
                .stream()
                .map(
                        FriendDTOMapper::toCustomerFriendDTO
                ).collect(Collectors.toSet());
    }
}
