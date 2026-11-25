package com.revenueos.organization;

import com.revenueos.auth.AuthTokenFactory;
import com.revenueos.auth.dto.AuthResponse;
import com.revenueos.auth.dto.RegisterCompanyRequest;
import com.revenueos.common.exception.ConflictException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.organization.dto.OrganizationResponse;
import com.revenueos.organization.dto.UpdateOrganizationRequest;
import com.revenueos.role.Role;
import com.revenueos.role.RoleService;
import com.revenueos.tenant.TenantContext;
import com.revenueos.user.User;
import com.revenueos.user.UserRepository;
import com.revenueos.user.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenFactory authTokenFactory;
    private final OrganizationMapper organizationMapper;

    public OrganizationService(OrganizationRepository organizationRepository,
                               UserRepository userRepository,
                               RoleService roleService,
                               PasswordEncoder passwordEncoder,
                               AuthTokenFactory authTokenFactory,
                               OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authTokenFactory = authTokenFactory;
        this.organizationMapper = organizationMapper;
    }

    /**
     * Self-service signup: creates the organization, seeds its default roles, and
     * provisions the first user as COMPANY_ADMIN. Returns tokens so the caller is
     * logged straight in.
     */
    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }

        Organization organization = new Organization();
        organization.setName(request.companyName());
        organization.setSlug(uniqueSlug(request.companyName()));
        organization.setStatus(OrganizationStatus.ACTIVE);
        organization.setSubscriptionPlan(SubscriptionPlan.FREE);
        organization.setTrialEndsAt(java.time.LocalDate.now().plusDays(20));
        organization = organizationRepository.save(organization);

        Role adminRole = roleService.provisionDefaults(organization.getId());

        User admin = new User();
        admin.setOrganizationId(organization.getId());
        admin.setName(request.adminName());
        admin.setEmail(request.email());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRoles(Set.of(adminRole));
        admin = userRepository.save(admin);

        return authTokenFactory.issueFor(admin);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse get(UUID id) {
        return organizationMapper.toResponse(requireAccessible(id));
    }

    @Transactional
    public OrganizationResponse update(UUID id, UpdateOrganizationRequest request) {
        Organization organization = requireAccessible(id);
        organization.setName(request.name());
        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    private Organization requireAccessible(UUID id) {
        if (!TenantContext.isPlatformAdmin() && !id.equals(TenantContext.getOrganizationId())) {
            throw ResourceNotFoundException.of("Organization", id);
        }
        return organizationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Organization", id));
    }

    private String uniqueSlug(String name) {
        String base = name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.isBlank()) {
            base = "org";
        }
        String slug = base;
        int suffix = 2;
        while (organizationRepository.existsBySlug(slug)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }
}
