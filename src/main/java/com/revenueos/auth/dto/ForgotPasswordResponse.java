package com.revenueos.auth.dto;

/**
 * Always returns the same message regardless of whether the email exists.
 * {@code resetToken} is populated only in non-production environments as a
 * convenience, since no email delivery is wired up yet.
 */
public record ForgotPasswordResponse(String message, String resetToken) {
}
