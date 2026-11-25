package com.revenueos.sales;

import com.revenueos.common.web.PageResponse;
import com.revenueos.sales.dto.LeadRequest;
import com.revenueos.sales.dto.LeadResponse;
import com.revenueos.sales.dto.OpportunityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(name = "Leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT_VIEW')")
    @Operation(operationId = "listLeads", summary = "List leads, optionally filtered by status")
    public PageResponse<LeadResponse> list(
            @RequestParam(required = false) LeadStatus status,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return leadService.list(status, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "createLead", summary = "Create a lead")
    public ResponseEntity<LeadResponse> create(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "updateLead", summary = "Update a lead")
    public LeadResponse update(@PathVariable UUID id, @Valid @RequestBody LeadRequest request) {
        return leadService.update(id, request);
    }

    @PostMapping("/{id}/convert")
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "convertLead", summary = "Convert a lead into an opportunity")
    public OpportunityResponse convert(@PathVariable UUID id) {
        return leadService.convert(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "deleteLead", summary = "Delete a lead")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        leadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
