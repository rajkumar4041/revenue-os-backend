package com.revenueos.security;

import com.revenueos.user.User;
import com.revenueos.user.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Security view of a {@link User}. Authorities are the flattened set of
 * permission codes from all assigned roles, which is what {@code @PreAuthorize}
 * checks against.
 */
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final UUID organizationId;
    private final String email;
    private final String passwordHash;
    private final boolean platformAdmin;
    private final UserStatus status;
    private final Set<GrantedAuthority> authorities;

    private UserPrincipal(UUID id, UUID organizationId, String email, String passwordHash,
                          boolean platformAdmin, UserStatus status, Set<GrantedAuthority> authorities) {
        this.id = id;
        this.organizationId = organizationId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.platformAdmin = platformAdmin;
        this.status = status;
        this.authorities = authorities;
    }

    public static UserPrincipal from(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            role.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getCode())));
        });
        return new UserPrincipal(user.getId(), user.getOrganizationId(), user.getEmail(),
                user.getPasswordHash(), user.isPlatformAdmin(), user.getStatus(), authorities);
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public boolean isPlatformAdmin() {
        return platformAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
