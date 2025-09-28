package com.damian.whatsapp.modules.chat.dto;

import java.time.Instant;

public record ChatMessage(
        String chatId,
        Long groupId,
        Long fromUserId,
        Long toUserId,
        String fromUserName,
        String chatType,
        String message,
        Instant timestamp
) {
}
