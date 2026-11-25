package com.revenueos.role;

import com.revenueos.role.dto.CreateRoleRequest;
import com.revenueos.role.dto.RoleResponse;
import com.revenueos.role.dto.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasAuthority('ROLE_MANAGE')")
@Tag(name = "Roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(operationId = "listRoles", summary = "List roles in the current organization")
    public List<RoleResponse> list() {
        return roleService.list();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getRole", summary = "Get a role by id")
    public RoleResponse get(@PathVariable UUID id) {
        return roleService.get(id);
    }

    @PostMapping
    @Operation(operationId = "createRole", summary = "Create a role")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateRole", summary = "Update a role")
    public RoleResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deleteRole", summary = "Delete a role")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
