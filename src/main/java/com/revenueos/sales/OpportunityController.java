package com.revenueos.sales;

import com.revenueos.sales.dto.OpportunityRequest;
import com.revenueos.sales.dto.OpportunityResponse;
import com.revenueos.sales.dto.OpportunityStageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/opportunities")
@Tag(name = "Opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT_VIEW')")
    @Operation(operationId = "listOpportunities", summary = "List the sales pipeline")
    public List<OpportunityResponse> list() {
        return opportunityService.list();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "createOpportunity", summary = "Create an opportunity")
    public ResponseEntity<OpportunityResponse> create(@Valid @RequestBody OpportunityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(opportunityService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "updateOpportunity", summary = "Update an opportunity")
    public OpportunityResponse update(@PathVariable UUID id, @Valid @RequestBody OpportunityRequest request) {
        return opportunityService.update(id, request);
    }

    @PatchMapping("/{id}/stage")
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "updateOpportunityStage", summary = "Move an opportunity to a different stage")
    public OpportunityResponse updateStage(@PathVariable UUID id, @Valid @RequestBody OpportunityStageRequest request) {
        return opportunityService.setStage(id, request.stage());
    }

    @PostMapping("/{id}/win")
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "winOpportunity", summary = "Mark an opportunity as won and hand over to a client")
    public OpportunityResponse win(@PathVariable UUID id) {
        return opportunityService.win(id);
    }
}
