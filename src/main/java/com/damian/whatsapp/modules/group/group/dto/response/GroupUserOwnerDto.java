package com.damian.whatsapp.modules.group.group.dto.response;

public record GroupUserOwnerDto(
        Long userId,
        String userName,
        String avatarFilename
) {
}
