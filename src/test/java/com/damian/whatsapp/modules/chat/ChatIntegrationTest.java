package com.damian.whatsapp.modules.chat;

import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatIntegrationTest extends AbstractIntegrationTest {
    @Container
    @ServiceConnection
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withReuse(true);

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    private static final String WS_URI = "ws://localhost:%d/ws";

    @BeforeEach
    void setup() throws Exception {
        User user = User.create()
                        .setEmail("user@demo.com")
                        .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                        .setRole(UserRole.ADMIN)
                        .setFirstName("John")
                        .setLastName("Wick")
                        .setGender(UserGender.MALE)
                        .setBirthdate(LocalDate.of(1989, 1, 1))
                        .setImageFilename("avatar.jpg");
        user.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(user);

        loginWithUser(user);
        SockJsClient sockJsClient = new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        );
        stompClient = new WebSocketStompClient(sockJsClient);
    }

    @Test
    public void shouldConnect() throws Exception {
        String url = String.format(WS_URI, port) + "?token=" + token;
        CompletableFuture<StompSession> future = new CompletableFuture<>();
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        stompClient.connect(
                url,
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        future.complete(session);
                    }

                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        future.completeExceptionally(exception);
                    }
                }
        );

        StompSession session = future.get(10, TimeUnit.SECONDS);
        assertTrue(session.isConnected());
    }
}