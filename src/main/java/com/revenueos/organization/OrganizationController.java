package com.revenueos.organization;

import com.revenueos.organization.dto.OrganizationResponse;
import com.revenueos.organization.dto.UpdateOrganizationRequest;
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
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{id}")
    public OrganizationResponse get(@PathVariable UUID id) {
        return organizationService.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZATION_MANAGE')")
    public OrganizationResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateOrganizationRequest request) {
        return organizationService.update(id, request);
    }
}
