package com.revenueos.user;

import com.revenueos.common.exception.ConflictException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.common.web.PageResponse;
import com.revenueos.role.Role;
import com.revenueos.role.RoleRepository;
import com.revenueos.tenant.TenantContext;
import com.revenueos.user.dto.CreateUserRequest;
import com.revenueos.user.dto.UpdateUserRequest;
import com.revenueos.user.dto.UserResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(Pageable pageable) {
        return PageResponse.of(
                userRepository.findByOrganizationId(TenantContext.getOrganizationId(), pageable),
                userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        return userMapper.toResponse(requireUser(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = new User();
        user.setOrganizationId(TenantContext.getOrganizationId());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(resolveRoles(request.roleIds()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = requireUser(id);
        user.setName(request.name());
        if (request.status() != null) {
            user.setStatus(request.status());
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse assignRoles(UUID id, Set<UUID> roleIds) {
        User user = requireUser(id);
        user.setRoles(resolveRoles(roleIds));
        return userMapper.toResponse(userRepository.save(user));
    }

    private User requireUser(UUID id) {
        return userRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    private Set<Role> resolveRoles(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Role> roles = roleRepository.findByOrganizationIdAndIdIn(TenantContext.getOrganizationId(), roleIds);
        if (roles.size() != roleIds.size()) {
            throw new ConflictException("One or more roles are invalid for this organization");
        }
        return roles;
    }
}
