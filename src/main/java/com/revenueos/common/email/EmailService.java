package com.revenueos.common.email;

import com.revenueos.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails. When {@code app.mail.enabled} is false (or no SMTP
 * is configured) it just logs — so local dev needs no mail server, and you can
 * point a real SMTP relay at Mailinator inboxes for testing.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final AppProperties properties;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public EmailService(AppProperties properties, ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.properties = properties;
        this.mailSenderProvider = mailSenderProvider;
    }

    public void sendPasswordReset(String to, String resetLink) {
        send(to, "Reset your RevenueOS password",
                "We received a request to reset your password.\n\n"
                        + "Reset it here: " + resetLink + "\n\n"
                        + "If you didn't request this, you can safely ignore this email.");
    }

    public void sendWelcome(String to, String name) {
        String baseUrl = properties.mail() != null ? properties.mail().baseUrl() : "";
        send(to, "Welcome to RevenueOS",
                "Hi " + name + ",\n\nYour RevenueOS account has been created. "
                        + "You can sign in at " + baseUrl + ".");
    }

    private void send(String to, String subject, String body) {
        AppProperties.Mail mail = properties.mail();
        if (mail == null || !mail.enabled()) {
            log.info("[mail disabled] would send '{}' to {}", subject, to);
            return;
        }
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.warn("Mail is enabled but no SMTP is configured (set MAIL_HOST); skipping email to {}", to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mail.from());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            sender.send(message);
            log.info("Sent '{}' to {}", subject, to);
        } catch (Exception ex) {
            // Never let a mail failure break the request that triggered it.
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }
}
