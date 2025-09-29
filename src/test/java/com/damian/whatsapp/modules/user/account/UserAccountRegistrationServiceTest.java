package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountRegistrationRequest;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountRegistrationService;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.service.UserService;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class UserAccountRegistrationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private UserAccountRegistrationService userAccountRegistrationService;

    @Mock
    private UserService userService;

    @Mock
    private UserAccountVerificationService userAccountVerificationService;

    @Test
    @DisplayName("should register a new account")
    void shouldRegisterAccount() {
        // given
        User givenUser = User.create()
                             .setEmail("user@test.com")
                             .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                             .setFirstName("John")
                             .setLastName("Wick")
                             .setPhone("123 123 123")
                             .setGender(UserGender.MALE)
                             .setBirthdate(LocalDate.of(1989, 1, 1))
                             .setImageFilename("no photoPath");

        UserAccountRegistrationRequest registrationRequest = new UserAccountRegistrationRequest(
                givenUser.getEmail(),
                givenUser.getPassword(),
                givenUser.getUserName(),
                givenUser.getFirstName(),
                givenUser.getLastName(),
                givenUser.getPhone(),
                givenUser.getBirthdate(),
                givenUser.getGender()
        );

        UserAccountToken userAccountToken = UserAccountToken.create()
                                                            .setAccount(givenUser.getAccount());

        // when
        when(userAccountVerificationService.generateVerificationToken(anyString())).thenReturn(userAccountToken);
        when(userService.createUser(any(UserAccountRegistrationRequest.class))).thenReturn(givenUser);

        User registeredUser = userAccountRegistrationService.registerAccount(registrationRequest);

        // then
        assertThat(registeredUser)
                .isNotNull()
                .extracting(
                        User::getEmail,
                        User::getFirstName,
                        User::getLastName,
                        User::getPhone,
                        User::getGender,
                        User::getBirthdate
                ).containsExactly(
                        givenUser.getEmail(),
                        givenUser.getFirstName(),
                        givenUser.getLastName(),
                        givenUser.getPhone(),
                        givenUser.getGender(),
                        givenUser.getBirthdate()
                );

    }
}
