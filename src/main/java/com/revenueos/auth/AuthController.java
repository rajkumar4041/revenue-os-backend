package com.revenueos.auth;

import com.revenueos.auth.dto.AuthResponse;
import com.revenueos.auth.dto.AuthUser;
import com.revenueos.auth.dto.ForgotPasswordRequest;
import com.revenueos.auth.dto.ForgotPasswordResponse;
import com.revenueos.auth.dto.LoginRequest;
import com.revenueos.auth.dto.LogoutRequest;
import com.revenueos.auth.dto.RegisterCompanyRequest;
import com.revenueos.auth.dto.ResetPasswordRequest;
import com.revenueos.auth.dto.TokenRefreshRequest;
import com.revenueos.common.web.MessageResponse;
import com.revenueos.organization.OrganizationService;
import com.revenueos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final OrganizationService organizationService;

    public AuthController(AuthService authService, OrganizationService organizationService) {
        this.authService = authService;
        this.organizationService = organizationService;
    }

    @PostMapping("/register-company")
    public ResponseEntity<AuthResponse> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.registerCompany(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public MessageResponse logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return new MessageResponse("Logged out");
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return new MessageResponse("Password updated");
    }

    @GetMapping("/me")
    public AuthUser me(@AuthenticationPrincipal UserPrincipal principal) {
        return authService.currentUser(principal.getId());
    }
}
