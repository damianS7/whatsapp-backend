package com.damian.whatsapp.shared;


import com.damian.whatsapp.modules.auth.dto.AuthenticationRequest;
import com.damian.whatsapp.modules.auth.dto.AuthenticationResponse;
import com.damian.whatsapp.modules.notification.NotificationRepository;
import com.damian.whatsapp.modules.setting.SettingRepository;
import com.damian.whatsapp.modules.user.account.UserAccountRepository;
import com.damian.whatsapp.modules.user.token.UserAccountTokenRepository;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {
    @Container
    @ServiceConnection
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withReuse(true);

    protected final String RAW_PASSWORD = "123456";

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserAccountTokenRepository userAccountTokenRepository;

    @Autowired
    protected UserAccountRepository userAccountRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SettingRepository settingRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    protected String token;

    @AfterAll
    void tearDown() {
        userAccountTokenRepository.deleteAll();
        notificationRepository.deleteAll();
        settingRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected void loginWithUser(User user) throws Exception {
        // given
        final HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        token = jwtUtil.generateToken(claims, user.getEmail());
    }

    protected void loginWithPost(User user) throws Exception {
        // given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                user.getEmail(), "123456"
        );

        String jsonRequest = objectMapper.writeValueAsString(authenticationRequest);

        // when
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonRequest))
                                  .andReturn();

        AuthenticationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        token = response.token();

    }
}