package com.revenueos.auth.token;

import com.revenueos.common.Tokens;
import com.revenueos.config.AppProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Manages opaque refresh tokens with rotation: each refresh revokes the token it
 * was presented with and issues a new one, so a leaked token is single-use.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final java.time.Duration ttl;

    public RefreshTokenService(RefreshTokenRepository repository, AppProperties properties) {
        this.repository = repository;
        this.ttl = properties.jwt().refreshTokenTtl();
    }

    @Transactional
    public String issue(UUID userId) {
        String raw = Tokens.random();
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setTokenHash(Tokens.sha256(raw));
        token.setExpiresAt(Instant.now().plus(ttl));
        repository.save(token);
        return raw;
    }

    /**
     * Validates and consumes a refresh token, returning the owning user id.
     * The presented token is revoked; callers issue a fresh one.
     */
    @Transactional
    public UUID rotate(String rawToken) {
        RefreshToken token = repository.findByTokenHash(Tokens.sha256(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (!token.isActive()) {
            throw new BadCredentialsException("Refresh token is expired or revoked");
        }
        token.setRevoked(true);
        repository.save(token);
        return token.getUserId();
    }

    @Transactional
    public void revoke(String rawToken) {
        repository.findByTokenHash(Tokens.sha256(rawToken)).ifPresent(token -> {
            token.setRevoked(true);
            repository.save(token);
        });
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        repository.revokeAllForUser(userId);
    }
}
