package com.revenueos.role;

import com.revenueos.common.exception.ConflictException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.permission.Permission;
import com.revenueos.permission.PermissionRepository;
import com.revenueos.role.dto.CreateRoleRequest;
import com.revenueos.role.dto.RoleResponse;
import com.revenueos.role.dto.UpdateRoleRequest;
import com.revenueos.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Creates the default system roles for a freshly registered organization and
     * returns the admin role so the first user can be attached to it.
     */
    @Transactional
    public Role provisionDefaults(UUID organizationId) {
        Role adminRole = null;
        for (Map.Entry<String, Set<String>> entry : DefaultRoleCatalog.all().entrySet()) {
            Role role = new Role();
            role.setOrganizationId(organizationId);
            role.setName(entry.getKey());
            role.setSystem(true);
            role.setDescription("Default " + entry.getKey() + " role");
            role.setPermissions(new HashSet<>(permissionRepository.findByCodeIn(entry.getValue())));
            role = roleRepository.save(role);
            if (DefaultRoleCatalog.COMPANY_ADMIN.equals(role.getName())) {
                adminRole = role;
            }
        }
        return adminRole;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> list() {
        return roleMapper.toResponseList(roleRepository.findByOrganizationIdOrderByName(TenantContext.getOrganizationId()));
    }

    @Transactional(readOnly = true)
    public RoleResponse get(UUID id) {
        return roleMapper.toResponse(requireRole(id));
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        if (roleRepository.existsByOrganizationIdAndName(organizationId, request.name())) {
            throw new ConflictException("A role named '" + request.name() + "' already exists");
        }
        Role role = new Role();
        role.setOrganizationId(organizationId);
        role.setName(request.name());
        role.setDescription(request.description());
        role.setSystem(false);
        role.setPermissions(resolvePermissions(request.permissionCodes()));
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse update(UUID id, UpdateRoleRequest request) {
        Role role = requireRole(id);
        if (request.description() != null) {
            role.setDescription(request.description());
        }
        if (request.permissionCodes() != null) {
            role.setPermissions(resolvePermissions(request.permissionCodes()));
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Transactional
    public void delete(UUID id) {
        Role role = requireRole(id);
        if (role.isSystem()) {
            throw new ConflictException("System roles cannot be deleted");
        }
        roleRepository.delete(role);
    }

    private Role requireRole(UUID id) {
        return roleRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Role", id));
    }

    private Set<Permission> resolvePermissions(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return new HashSet<>();
        }
        List<Permission> found = permissionRepository.findByCodeIn(codes);
        if (found.size() != codes.size()) {
            throw new ConflictException("One or more permission codes are invalid");
        }
        return new HashSet<>(found);
    }
}
