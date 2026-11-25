package com.revenueos.audit;

import com.revenueos.audit.dto.AuditResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditResponse toResponse(AuditLog auditLog);
}
