package com.damian.whatsapp.chat;

import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.customer.Customer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatManagement {
    //    private Socket roomChannel;
    //    private Set<String> bannedUsers = new HashSet<>();
    //    private Set<String> mutedUsers = new HashSet<>();

    private final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    public void addSessionToRoom(String roomId, String sessionId) {
        roomUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void removeSession(String sessionId) {
        roomUsers.values().forEach(sessions -> sessions.remove(sessionId));
        roomUsers.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public Set<String> getSessionsForRoom(String roomId) {
        return roomUsers.getOrDefault(roomId, Set.of());
    }

    public void addUserToRoom(Long id) {
        Customer customer = AuthHelper.getLoggedCustomer();
        // check if banned
        // check room exists
        // check if user is in room
        //        usersInRoom.add(customer);
    }

    public void removeUserToRoom(Long id) {
        // check if banned
        // check room exists
        // check if user is in room
    }

    public String messageToRoom(Long id, String message) {
        // check if banned
        // check room exists
        // check if user is in room
        return "";
    }
}
