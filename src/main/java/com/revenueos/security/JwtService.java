package com.revenueos.security;

import com.revenueos.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Issues and verifies short-lived access tokens. Refresh tokens are opaque and
 * handled separately by {@code RefreshTokenService}.
 */
@Service
public class JwtService {

    private static final String CLAIM_ORG = "orgId";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_PLATFORM_ADMIN = "platformAdmin";

    private final SecretKey key;
    private final String issuer;
    private final long accessTtlSeconds;

    public JwtService(AppProperties properties) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.jwt().secret()));
        this.issuer = properties.jwt().issuer();
        this.accessTtlSeconds = properties.jwt().accessTokenTtl().getSeconds();
    }

    public String generateAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(principal.getId().toString())
                .claim(CLAIM_ORG, principal.getOrganizationId().toString())
                .claim(CLAIM_EMAIL, principal.getUsername())
                .claim(CLAIM_PLATFORM_ADMIN, principal.isPlatformAdmin())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .signWith(key)
                .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
