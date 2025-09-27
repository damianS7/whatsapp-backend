package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.exception.UserAccountEmailTakenException;
import com.damian.whatsapp.modules.user.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.whatsapp.modules.user.account.service.UserAccountService;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.modules.user.user.web.rest.dto.request.UserAccountEmailUpdateRequest;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccount;
import com.damian.whatsapp.shared.exception.Exceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserAccountServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountService userAccountService;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.create()
                   .setId(2L)
                   .setEmail("user@demo.com")
                   .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                   .setRole(UserRole.USER)
                   .setUserName("John")
                   .setFirstName("John")
                   .setLastName("Wick")
                   .setGender(UserGender.MALE)
                   .setBirthdate(LocalDate.of(1989, 1, 1))
                   .setImageFilename("avatar.jpg");
    }

    @Test
    @DisplayName("Should update email")
    void shouldUpdateEmail() {
        // given
        // set the user on the context
        setUpContext(user);

        UserAccountEmailUpdateRequest updateRequest = new UserAccountEmailUpdateRequest(
                RAW_PASSWORD,
                "david@test.com"
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByUserAccount_Email(anyString())).thenReturn(false);
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        UserAccount updatedAccount = userAccountService.updateEmail(updateRequest);

        // then
        assertThat(updatedAccount)
                .isNotNull()
                .extracting(
                        UserAccount::getEmail
                ).isEqualTo(
                        updateRequest.newEmail()
                );

        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }

    @Test
    @DisplayName("Should not update email when is already taken")
    void shouldNotUpdateEmailWhenIsAlreadyTaken() {
        // given

        // set the user on the context
        setUpContext(user);

        UserAccountEmailUpdateRequest updateRequest = new UserAccountEmailUpdateRequest(
                RAW_PASSWORD,
                "david2@test.com"
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByUserAccount_Email(updateRequest.newEmail())).thenReturn(true);

        UserAccountEmailTakenException exception = assertThrows(
                UserAccountEmailTakenException.class,
                () -> userAccountService.updateEmail(updateRequest)
        );

        // then
        assertEquals(Exceptions.USER.EMAIL_TAKEN, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update email when password is wrong")
    void shouldNotUpdateEmailWhenPasswordIsWrong() {
        // given
        // set the user on the context
        setUpContext(user);

        UserAccountEmailUpdateRequest updateRequest = new UserAccountEmailUpdateRequest(
                "wrong password",
                "david@test.com"
        );

        // when
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> userAccountService.updateEmail(updateRequest)
        );

        // then
        assertEquals(Exceptions.ACCOUNT.INVALID_PASSWORD, exception.getMessage());
    }

}
