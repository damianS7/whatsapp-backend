package com.damian.whatsapp.config.security;

import com.damian.whatsapp.modules.auth.exception.EmailNotFoundException;
import com.damian.whatsapp.modules.auth.service.CustomUserDetailsService;
import com.damian.whatsapp.shared.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtHandshakeInterceptor(
            JwtUtil jwtUtil,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        URI uri = request.getURI();
        String token = UriComponentsBuilder.fromUri(uri)
                                           .build()
                                           .getQueryParams()
                                           .getFirst("token");

        if (token != null && jwtUtil.isTokenValid(token)) {
            // Extract the email from the JWT.
            final String email = jwtUtil.extractEmail(token);

            // If the email found in token is not null and there is no Authentication object
            // in the SecurityContext, then we can go ahead and authenticate the user.
            if (email != null) {
                UserDetails userDetails;
                try {
                    // Load the user details from the database.
                    userDetails = customUserDetailsService.loadUserByEmail(email);
                } catch (EmailNotFoundException exception) {
                    return false;
                }

                // Create an Authentication object.
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Finally, set the Authentication object in the SecurityContext.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            return true;
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}