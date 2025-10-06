package com.damian.whatsapp.config.security;

import com.damian.whatsapp.modules.auth.service.CustomUserDetailsService;
import com.damian.whatsapp.shared.util.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtChannelInterceptor(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                // 1) intenta obtener token desde headers STOMP
                String token = accessor.getFirstNativeHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                // 2) si no viene en headers, intenta desde los sessionAttributes (lo puso el HandshakeInterceptor)
                if ((token == null || token.isEmpty()) && accessor.getSessionAttributes() != null) {
                    Object t = accessor.getSessionAttributes().get("token");
                    if (t != null) {
                        token = t.toString();
                    }
                }

                if (token == null || !jwtUtil.isTokenValid(token)) {
                    throw new IllegalArgumentException("Token inválido o ausente");
                }

                String email = jwtUtil.extractEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByEmail(email);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Asigna el Principal de la sesión STOMP
                accessor.setUser(auth);

                // También puedes colocar el context aquí si lo necesitas durante el CONNECT
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // Para mensajes no-CONNECT: toma el Principal ya asignado y colócalo en el SecurityContext
                Principal user = accessor.getUser();
                if (user instanceof Authentication authentication) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            return message;
        } finally {
            // No limpiar aquí: lo hacemos en afterSendCompletion para que la ejecución del mensaje vea el contexto
        }
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        // Limpiamos el contexto para evitar fugas entre hilos
        SecurityContextHolder.clearContext();
    }
}