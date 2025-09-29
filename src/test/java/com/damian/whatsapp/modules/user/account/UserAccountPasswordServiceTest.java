package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountPasswordResetRequest;
import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountPasswordService;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.whatsapp.modules.user.account.token.UserAccountTokenRepository;
import com.damian.whatsapp.modules.user.account.token.UserAccountTokenType;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.infrastructure.mail.EmailSenderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserAccountPasswordServiceTest extends AbstractServiceTest {

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAccountPasswordService userAccountPasswordService;

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

    @Mock
    private UserAccountVerificationService userAccountVerificationService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("Should update account password")
    void shouldUpdateAccountPassword() {
        // given
        final String rawNewPassword = "1234";
        final String encodedNewPassword = passwordEncoder.encode(rawNewPassword);

        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                rawNewPassword
        );

        // set the user on the context
        setUpContext(user);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userAccountPasswordService.updatePassword(updateRequest);

        // then
        verify(userRepository, times(1)).save(user);
        assertThat(user.getPassword()).isEqualTo(encodedNewPassword);
    }

    @Test
    @DisplayName("Should not update password when current password failed")
    void shouldNotUpdatePasswordWhenPasswordConfirmationFailed() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        // set the user on the context
        setUpContext(user);

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                "wrongPassword",
                "1234"
        );

        // when
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> userAccountPasswordService.updatePassword(
                        updateRequest
                )
        );
        // then
        assertEquals(Exceptions.ACCOUNT.INVALID_PASSWORD, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update password when account not found")
    void shouldNotUpdatePasswordWhenAccountNotFound() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        // set the user on the context
        setUpContext(user);

        UserAccountPasswordUpdateRequest updateRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                "1234678Ax$"
        );

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userAccountPasswordService.updatePassword(
                        updateRequest
                )
        );

        // then
        assertEquals(Exceptions.USER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should reset password")
    void shouldGeneratePasswordResetToken() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordResetRequest passwordResetRequest = new UserAccountPasswordResetRequest(
                user.getEmail()
        );

        // when
        when(userRepository.findByUserAccount_Email(user.getEmail())).thenReturn(Optional.of(user));
        when(userAccountTokenRepository.save(any(UserAccountToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccountToken generatedToken = userAccountPasswordService.generatePasswordResetToken(passwordResetRequest);

        // then
        assertThat(generatedToken)
                .isNotNull();
        assertThat(generatedToken.getToken().length()).isGreaterThanOrEqualTo(5);
        verify(userAccountTokenRepository, times(1)).save(any(UserAccountToken.class));
    }

    @Test
    @DisplayName("Should not create password reset token when account not found")
    void shouldNotGeneratePasswordResetTokenWhenAccountNotFound() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordResetRequest passwordResetRequest = new UserAccountPasswordResetRequest(
                user.getEmail()
        );

        UserAccountToken token = new UserAccountToken(user.getAccount());
        token.setToken(token.generateToken());
        token.setType(UserAccountTokenType.RESET_PASSWORD);

        // when
        when(userRepository.findByUserAccount_Email(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(
                UserAccountNotFoundException.class,
                () -> userAccountPasswordService.generatePasswordResetToken(passwordResetRequest)
        );

        // then
        verify(userRepository, times(1)).findByUserAccount_Email(anyString());
    }

    @Test
    @DisplayName("Should set a new password after reset password")
    void shouldSetPasswordAfterGeneratePasswordResetToken() {
        // given
        final String rawNewPassword = "1111000";
        final String encodedNewPassword = passwordEncoder.encode(rawNewPassword);

        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordResetSetRequest passwordResetRequest = new UserAccountPasswordResetSetRequest(
                rawNewPassword
        );

        UserAccountToken token = new UserAccountToken(user.getAccount());
        token.setToken(token.generateToken());
        token.setType(UserAccountTokenType.RESET_PASSWORD);

        // when
        //        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userAccountVerificationService.validateToken(token.getToken())).thenReturn(token);
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(emailSenderService).send(anyString(), anyString(), anyString());
        userAccountPasswordService.passwordResetWithToken(token.getToken(), passwordResetRequest);

        // then
        assertEquals(user.getPassword(), encodedNewPassword);
    }
}
