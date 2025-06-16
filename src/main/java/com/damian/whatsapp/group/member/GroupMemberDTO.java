package com.damian.whatsapp.group.member;

public record GroupMemberDTO(
        Long id,
        Long groupId,
        Long memberCustomerId,
        String customerName,
        String avatarFilename
) {
}
