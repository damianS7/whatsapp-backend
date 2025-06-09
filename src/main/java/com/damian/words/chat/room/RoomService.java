package com.damian.words.chat.room;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RoomService(
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
}
