package com.damian.whatsapp.chat;

import com.damian.whatsapp.config.TestSecurityConfig;
import com.damian.whatsapp.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @MockitoBean
    private ChatManagement chatManagement; // Simulado
    private Customer customer;
    private static final String WS_URI = "ws://localhost:%d/ws";

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Disabled
    @Test
    public void shouldConnect() throws Exception {
        CompletableFuture<StompSession> future = new CompletableFuture<>();

        stompClient.connect(
                String.format(WS_URI, port),
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        future.complete(session);
                    }
                }
        );

        StompSession session = future.get(3, TimeUnit.SECONDS);
        assert session.isConnected();

        // Verifica que se llamó a roomManagement.addSessionToRoom
        //        verify(roomManagement, timeout(2000)).addSessionToRoom(eq("123"), any());
    }

    @Disabled
    @Test
    public void shouldConnect2() throws Exception {
        CompletableFuture<StompSession> future = new CompletableFuture<>();

        stompClient.connect(
                String.format(WS_URI, port),
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

        StompSession session = future.get(5, TimeUnit.SECONDS); // sube a 5s por seguridad
        assertTrue(session.isConnected());
    }
}