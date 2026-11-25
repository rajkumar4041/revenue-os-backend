package com.revenueos.auth;

import com.revenueos.auth.dto.AuthResponse;
import com.revenueos.auth.dto.AuthUser;
import com.revenueos.auth.dto.ForgotPasswordRequest;
import com.revenueos.auth.dto.ForgotPasswordResponse;
import com.revenueos.auth.dto.LoginRequest;
import com.revenueos.auth.dto.ResetPasswordRequest;
import com.revenueos.auth.dto.TokenRefreshRequest;
import com.revenueos.auth.token.PasswordResetToken;
import com.revenueos.auth.token.PasswordResetTokenRepository;
import com.revenueos.auth.token.RefreshTokenService;
import com.revenueos.common.Tokens;
import com.revenueos.common.email.EmailService;
import com.revenueos.config.AppProperties;
import com.revenueos.user.User;
import com.revenueos.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private static final String RESET_MESSAGE =
            "If an account exists for that email, a password reset link has been sent.";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenFactory authTokenFactory;
    private final EmailService emailService;
    private final java.time.Duration resetTtl;
    private final boolean mailEnabled;
    private final String baseUrl;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RefreshTokenService refreshTokenService,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthTokenFactory authTokenFactory,
                       EmailService emailService,
                       AppProperties properties) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenFactory = authTokenFactory;
        this.emailService = emailService;
        this.resetTtl = properties.security().passwordResetTtl();
        this.mailEnabled = properties.mail() != null && properties.mail().enabled();
        this.baseUrl = properties.mail() != null ? properties.mail().baseUrl() : "";
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmailWithRoles(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        return authTokenFactory.issueFor(user);
    }

    @Transactional
    public AuthResponse refresh(TokenRefreshRequest request) {
        UUID userId = refreshTokenService.rotate(request.refreshToken());
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        return authTokenFactory.issueFor(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        return userRepository.findByEmail(request.email())
                .map(user -> {
                    String raw = Tokens.random();
                    PasswordResetToken token = new PasswordResetToken();
                    token.setUserId(user.getId());
                    token.setTokenHash(Tokens.sha256(raw));
                    token.setExpiresAt(Instant.now().plus(resetTtl));
                    passwordResetTokenRepository.save(token);

                    String resetLink = baseUrl + "/reset-password?token=" + raw;
                    emailService.sendPasswordReset(user.getEmail(), resetLink);

                    // When email is actually sent we don't leak the token in the response;
                    // without SMTP (local dev) we return it so the flow stays usable.
                    return new ForgotPasswordResponse(RESET_MESSAGE, mailEnabled ? null : raw);
                })
                .orElse(new ForgotPasswordResponse(RESET_MESSAGE, null));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(Tokens.sha256(request.token()))
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));
        if (!token.isUsable()) {
            throw new BadCredentialsException("Invalid or expired reset token");
        }
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        // Any session minted with the old password should no longer refresh.
        refreshTokenService.revokeAllForUser(user.getId());
    }

    @Transactional(readOnly = true)
    public AuthUser currentUser(UUID userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new BadCredentialsException("Session is no longer valid"));
        return authTokenFactory.describe(user);
    }
}
