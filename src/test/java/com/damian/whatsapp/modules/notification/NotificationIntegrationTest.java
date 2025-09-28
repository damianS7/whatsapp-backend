package com.damian.whatsapp.modules.notification;

import com.damian.whatsapp.modules.notification.dto.NotificationEvent;
import com.damian.whatsapp.modules.notification.dto.response.NotificationDto;
import com.damian.whatsapp.modules.notification.service.NotificationService;
import com.damian.whatsapp.modules.user.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.Notification;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserPrincipal;
import com.damian.whatsapp.shared.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationIntegrationTest extends AbstractIntegrationTest {
    private User user;

    @Autowired
    private JwtUtil jwtUtil;

    @SpyBean
    private NotificationService notificationService;

    @BeforeAll
    void setUp() {
        user = User.create()
                   .setEmail("customer@test.com")
                   .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                   .setRole(UserRole.ADMIN)
                   .setFirstName("John")
                   .setLastName("Wick")
                   .setUserName("johnwick")
                   .setGender(UserGender.MALE)
                   .setBirthdate(LocalDate.of(1989, 1, 1))
                   .setImageFilename("avatar.jpg");
        user.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(user);
    }

    @Test
    @DisplayName("Should get notifications for the logged user")
    void shouldGetNotifications() throws Exception {
        // given
        Notification notification = Notification
                .create(user)
                .setMessage("Alice has follow you.")
                .setType(NotificationType.FOLLOW)
                .setMetadata(
                        Map.of(
                                "userName", "alice"
                        )
                );

        notificationRepository.save(notification);

        // when
        loginWithUser(user);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .get("/api/v1/notifications")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        NotificationDto[] notificationsDto = objectMapper.treeToValue(contentNode, NotificationDto[].class);

        // then
        assertThat(notificationsDto.length).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should delete notifications for the logged user")
    void shouldDeleteNotifications() throws Exception {
        // given
        Notification notification = Notification
                .create(user)
                .setMessage("Alice has follow you.")
                .setType(NotificationType.FOLLOW)
                .setMetadata(
                        Map.of(
                                "userName", "alice"
                        )
                );

        notificationRepository.save(notification);

        // when
        loginWithUser(user);
        mockMvc.perform(MockMvcRequestBuilders
                       .delete("/api/v1/notifications")
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    @DisplayName("Should get real time notifications for the logged user")
    void shouldGetRealTimeNotifications() throws Exception {
        // given
        loginWithUser(user);

        NotificationEvent notificationEvent = new NotificationEvent(
                user.getId(),
                NotificationType.LIKE,
                Map.of("postId", 123),
                "Message",
                "2025-09-10T00:00:00"
        );

        UserPrincipal user = new UserPrincipal(this.user);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // when
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        when(notificationService.getNotificationsForUser())
                .thenReturn(Flux.just(notificationEvent));

        MvcResult result = mockMvc.perform(get("/api/v1/notifications/stream")
                                          .accept(MediaType.TEXT_EVENT_STREAM)
                                          .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                  .andExpect(status().is(HttpStatus.OK.value()))
                                  .andReturn();

        String rawResponse = result.getResponse().getContentAsString();
        // Cada evento SSE va en una línea, empieza con "data:"
        String json = rawResponse.replaceFirst("data:", "").trim();

        NotificationDto notificationDto = objectMapper.readValue(
                json,
                NotificationDto.class
        );

        assertThat(notificationDto)
                .isNotNull()
                .extracting(
                        NotificationDto::type,
                        NotificationDto::message,
                        NotificationDto::metadata,
                        NotificationDto::createdAt
                ).containsExactly(
                        notificationEvent.type(),
                        notificationEvent.message(),
                        notificationEvent.metadata(),
                        notificationEvent.createdAt()
                );
    }
}
