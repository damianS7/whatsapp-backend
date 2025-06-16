package com.damian.whatsapp.chat;

import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.common.utils.JWTUtil;
import com.damian.whatsapp.customer.Customer;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class ChatEventListener {

    private final JWTUtil jwtUtil;
    private final ChatManagement chatManagement;

    public ChatEventListener(JWTUtil jwtUtil, ChatManagement chatManagement) {
        this.jwtUtil = jwtUtil;
        this.chatManagement = chatManagement;
    }

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        System.out.println("HANDLING CONNECT");
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String token = accessor.getFirstNativeHeader("Authorization");
        if (!jwtUtil.isTokenValid(token, loggedCustomer)) {
            throw new IllegalArgumentException("Invalid token");
        }

        // Validar token, loguear conexión, etc.
        System.out.println("Established connection: " + sessionId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        chatManagement.removeSession(sessionId);
        System.out.println("Sesión desconectada: " + sessionId);
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination(); // e.g., "/rooms/1"
        String roomId = accessor.getFirstNativeHeader("roomId");

        if (roomId != null) {
            chatManagement.addSessionToRoom(roomId, sessionId);
            System.out.println("Conectado a sala: " + roomId + " | Sesión: " + sessionId);
        }

        //        String roomId = destination.replace("/rooms/", "");
        //        roomManagement.addSessionToRoom(roomId, sessionId);
    }

    // TODO: handle unsubscribe
}