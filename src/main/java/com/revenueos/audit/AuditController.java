package com.revenueos.audit;

import com.revenueos.audit.dto.AuditResponse;
import com.revenueos.common.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Activity")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(operationId = "listActivity", summary = "Organization activity timeline")
    public PageResponse<AuditResponse> list(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return auditService.list(pageable);
    }
}
