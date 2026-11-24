package com.revenueos.auth;

import com.revenueos.auth.dto.AuthResponse;
import com.revenueos.auth.dto.AuthUser;
import com.revenueos.auth.token.RefreshTokenService;
import com.revenueos.config.AppProperties;
import com.revenueos.security.JwtService;
import com.revenueos.security.UserPrincipal;
import com.revenueos.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Builds the {@link AuthResponse} returned by login, registration and refresh:
 * a fresh access token plus a rotated refresh token and the identity payload.
 * The user must be loaded with its roles and permissions initialized.
 */
@Component
public class AuthTokenFactory {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final long accessTtlSeconds;

    public AuthTokenFactory(JwtService jwtService, RefreshTokenService refreshTokenService, AppProperties properties) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.accessTtlSeconds = properties.jwt().accessTokenTtl().getSeconds();
    }

    public AuthResponse issueFor(User user) {
        UserPrincipal principal = UserPrincipal.from(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = refreshTokenService.issue(user.getId());
        return new AuthResponse(accessToken, refreshToken, "Bearer", accessTtlSeconds, describe(user));
    }

    public AuthUser describe(User user) {
        Set<String> roles = new TreeSet<>();
        Set<String> permissions = new TreeSet<>();
        user.getRoles().forEach(role -> {
            roles.add(role.getName());
            role.getPermissions().forEach(p -> permissions.add(p.getCode()));
        });
        return new AuthUser(user.getId(), user.getName(), user.getEmail(),
                user.getOrganizationId(), List.copyOf(roles), List.copyOf(permissions));
    }
}
