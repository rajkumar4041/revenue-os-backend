package com.revenueos.common;

import com.revenueos.auth.dto.RegisterCompanyRequest;
import com.revenueos.organization.OrganizationService;
import com.revenueos.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Seeds a ready-to-use demo company through the normal registration path so the
 * app is immediately explorable. Login: admin@demo.io / Password123!
 */
@Component
@Profile({"dev", "docker", "demo"})
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);
    private static final String DEMO_EMAIL = "admin@demo.io";

    private final UserRepository userRepository;
    private final OrganizationService organizationService;

    public DemoDataInitializer(UserRepository userRepository, OrganizationService organizationService) {
        this.userRepository = userRepository;
        this.organizationService = organizationService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(DEMO_EMAIL)) {
            return;
        }
        organizationService.registerCompany(new RegisterCompanyRequest(
                "Demo Company", "Demo Admin", DEMO_EMAIL, "Password123!"));
        log.info("Seeded demo company. Login with {} / Password123!", DEMO_EMAIL);
    }
}
