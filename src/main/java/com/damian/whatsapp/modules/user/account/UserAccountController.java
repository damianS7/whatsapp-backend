package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.dto.request.*;
import com.damian.whatsapp.modules.user.account.service.UserAccountPasswordService;
import com.damian.whatsapp.modules.user.account.service.UserAccountRegistrationService;
import com.damian.whatsapp.modules.user.account.service.UserAccountService;
import com.damian.whatsapp.modules.user.account.service.UserAccountVerificationService;
import com.damian.whatsapp.modules.user.user.dto.mapper.UserDtoMapper;
import com.damian.whatsapp.modules.user.user.dto.response.UserDto;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccount;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserAccountController {
    private final UserAccountRegistrationService userAccountRegistrationService;
    private final UserAccountPasswordService userAccountPasswordService;
    private final UserAccountVerificationService userAccountVerificationService;
    private final UserAccountService userAccountService;

    public UserAccountController(
            UserAccountRegistrationService userAccountRegistrationService,
            UserAccountPasswordService userAccountPasswordService,
            UserAccountVerificationService userAccountVerificationService,
            UserAccountService userAccountService
    ) {
        this.userAccountRegistrationService = userAccountRegistrationService;
        this.userAccountPasswordService = userAccountPasswordService;
        this.userAccountVerificationService = userAccountVerificationService;
        this.userAccountService = userAccountService;
    }

    // endpoint to modify current user email
    @PatchMapping("/accounts/email")
    public ResponseEntity<UserDto> updateEmail(
            @Validated @RequestBody
            UserAccountEmailUpdateRequest request
    ) {
        UserAccount userAccount = userAccountService.updateEmail(request);
        UserDto userDto = UserDtoMapper.toUserDto(userAccount.getOwner());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    // endpoint for account registration
    @PostMapping("/accounts/register")
    public ResponseEntity<?> register(
            @Validated @RequestBody
            UserAccountRegistrationRequest request
    ) {
        User registeredUser = userAccountRegistrationService.registerAccount(request);

        UserDto dto = UserDtoMapper.toUserDto(registeredUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    // endpoint to modify current user password
    @PatchMapping("/accounts/password")
    public ResponseEntity<?> updatePassword(
            @Validated @RequestBody
            UserAccountPasswordUpdateRequest request
    ) {
        userAccountPasswordService.updatePassword(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    // endpoint for account verification
    @GetMapping("/accounts/verification/{token:.+}")
    public ResponseEntity<?> verifyAccount(
            @PathVariable @NotBlank
            String token
    ) {
        // verification the account using the provided token
        UserAccount account = userAccountVerificationService.verifyAccount(token);

        // send email to user after account has been verificated
        userAccountVerificationService.sendAccountVerifiedEmail(account);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(com.damian.whatsapp.shared.util.ApiResponse.success(
                        "Your account has been verified. You can now log in with your credentials."));
    }

    // endpoint for account to request for account verification email
    @PostMapping("/accounts/resend-verification")
    public ResponseEntity<?> resendVerification(
            @Validated @RequestBody
            UserAccountVerificationResendRequest request
    ) {
        // generate a new verification token
        UserAccountToken userAccountToken = userAccountVerificationService.generateVerificationToken(request.email());

        // send the account verification link
        userAccountVerificationService.sendAccountVerificationLinkEmail(request.email(), userAccountToken.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(com.damian.whatsapp.shared.util.ApiResponse.success(
                        "A verification link has been sent to your email."));
    }

    // endpoint to request for a reset password
    @PostMapping("/accounts/reset-password")
    public ResponseEntity<?> resetPasswordRequest(
            @Validated @RequestBody
            UserAccountPasswordResetRequest request
    ) {
        // generate a new password reset token
        UserAccountToken userAccountToken = userAccountPasswordService.generatePasswordResetToken(request);

        // send the email with the link to reset the password
        userAccountPasswordService.sendResetPasswordEmail(
                userAccountToken.getAccount().getEmail(),
                userAccountToken.getToken()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(com.damian.whatsapp.shared.util.ApiResponse.success(
                        "A password reset link has been sent to your email address."));
    }

    // endpoint to set a new password using token
    @PostMapping("/accounts/reset-password/{token:.+}")
    public ResponseEntity<?> resetPassword(
            @PathVariable @NotBlank
            String token,
            @Validated @RequestBody
            UserAccountPasswordResetSetRequest request
    ) {
        // update the password using the token
        userAccountPasswordService.passwordResetWithToken(token, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(com.damian.whatsapp.shared.util.ApiResponse.success("Password reset successfully."));
    }
}