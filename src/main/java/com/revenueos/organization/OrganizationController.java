package com.revenueos.organization;

import com.revenueos.organization.dto.OrganizationResponse;
import com.revenueos.organization.dto.UpdateOrganizationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getOrganization", summary = "Get an organization by id")
    public OrganizationResponse get(@PathVariable UUID id) {
        return organizationService.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZATION_MANAGE')")
    @Operation(operationId = "updateOrganization", summary = "Update an organization")
    public OrganizationResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateOrganizationRequest request) {
        return organizationService.update(id, request);
    }
}
