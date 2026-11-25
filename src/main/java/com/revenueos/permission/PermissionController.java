package com.revenueos.permission;

import com.revenueos.permission.dto.PermissionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(operationId = "listPermissions", summary = "List all permission codes")
    public List<PermissionResponse> list() {
        return permissionService.listAll();
    }
}
