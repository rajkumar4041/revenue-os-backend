package com.revenueos.auth.token;

import com.revenueos.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Server-side record of an issued refresh token. Only the SHA-256 hash of the
 * token is stored; it is rotated on every use and revoked on logout.
 */
@Entity
@Table(name = "refresh_tokens", uniqueConstraints = @UniqueConstraint(name = "uk_refresh_tokens_hash", columnNames = "token_hash"))
@Getter
@Setter
public class RefreshToken extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    public boolean isActive() {
        return !revoked && expiresAt.isAfter(Instant.now());
    }
}
