package com.revenueos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Strongly-typed access to the {@code app.*} configuration block.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Security security, Cors cors, Mail mail) {

    public record Jwt(String issuer, String secret, Duration accessTokenTtl, Duration refreshTokenTtl) {}

    public record Security(Duration passwordResetTtl) {}

    public record Cors(String allowedOrigins) {}

    /** Email settings. When {@code enabled} is false, messages are logged instead of sent. */
    public record Mail(boolean enabled, String from, String baseUrl) {}
}
