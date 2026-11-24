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

@Entity
@Table(name = "password_reset_tokens", uniqueConstraints = @UniqueConstraint(name = "uk_password_reset_tokens_hash", columnNames = "token_hash"))
@Getter
@Setter
public class PasswordResetToken extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    public boolean isUsable() {
        return !used && expiresAt.isAfter(Instant.now());
    }
}
