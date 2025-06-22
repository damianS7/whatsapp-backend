package com.damian.whatsapp.group.member;

public record GroupMemberDTO(
        Long id,
        Long groupId,
        Long customerId,
        String customerName,
        String avatarFilename
) {
}
