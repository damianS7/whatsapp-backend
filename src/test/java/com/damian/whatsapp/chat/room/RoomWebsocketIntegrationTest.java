package com.damian.whatsapp.chat.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoomWebsocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @MockitoBean
    private RoomManagement roomManagement; // Simulado

    private static final String WS_URI = "ws://localhost:%d/ws";

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

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

        // Espera a que se conecte o lance error
        future.get(3, TimeUnit.SECONDS);

        // Verifica que se llamó a roomManagement.addSessionToRoom
        //        verify(roomManagement, timeout(2000)).addSessionToRoom(eq("123"), any());
    }
}