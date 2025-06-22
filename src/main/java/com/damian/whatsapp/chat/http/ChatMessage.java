package com.damian.whatsapp.chat.http;

import java.time.Instant;

public class ChatMessage {
    public Long groupId;
    public Long fromCustomerId;
    public Long toCustomerId;
    public String fromCustomerName;
    public String chatType;
    public String message;
    public Instant timestamp;
}
