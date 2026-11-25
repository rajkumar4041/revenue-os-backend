package com.revenueos.audit;

import com.revenueos.audit.dto.AuditResponse;
import com.revenueos.common.web.PageResponse;
import com.revenueos.tenant.TenantContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Append-only activity trail. {@link #record} is called from services as things
 * happen; the feed powers the activity timeline.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditMapper auditMapper;

    public AuditService(AuditLogRepository auditLogRepository, AuditMapper auditMapper) {
        this.auditLogRepository = auditLogRepository;
        this.auditMapper = auditMapper;
    }

    @Transactional
    public void record(String action, String entityType, UUID entityId, String summary) {
        if (!TenantContext.isBound()) {
            return;
        }
        AuditLog entry = new AuditLog();
        entry.setOrganizationId(TenantContext.getOrganizationId());
        entry.setUserId(TenantContext.getUserId());
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setMetadata(summary);
        auditLogRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditResponse> list(Pageable pageable) {
        return PageResponse.of(
                auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(TenantContext.getOrganizationId(), pageable),
                auditMapper::toResponse);
    }
}
