package com.damian.whatsapp.modules.group.group.dto.response;

public record GroupMemberDto(
        Long id,
        Long groupId,
        Long userId,
        String userName,
        String userAvatarFilename
) {
}
