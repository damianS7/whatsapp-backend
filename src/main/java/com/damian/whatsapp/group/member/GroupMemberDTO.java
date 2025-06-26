package com.damian.whatsapp.group.member;

public record GroupMemberDTO(
        Long id,
        Long groupId,
        // TODO remove?
        Long customerId,
        String customerName,
        String customerAvatarFilename
) {
}
