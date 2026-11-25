package com.revenueos.sales;

import com.revenueos.common.exception.ApiException;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.common.web.PageResponse;
import com.revenueos.sales.dto.LeadRequest;
import com.revenueos.sales.dto.LeadResponse;
import com.revenueos.sales.dto.OpportunityResponse;
import com.revenueos.tenant.TenantContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final SalesMapper salesMapper;

    public LeadService(LeadRepository leadRepository, OpportunityRepository opportunityRepository, SalesMapper salesMapper) {
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.salesMapper = salesMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<LeadResponse> list(LeadStatus status, Pageable pageable) {
        return PageResponse.of(leadRepository.search(TenantContext.getOrganizationId(), status, pageable), salesMapper::toResponse);
    }

    @Transactional
    public LeadResponse create(LeadRequest request) {
        Lead lead = new Lead();
        lead.setOrganizationId(TenantContext.getOrganizationId());
        apply(lead, request);
        if (request.status() != null) {
            lead.setStatus(request.status());
        }
        return salesMapper.toResponse(leadRepository.save(lead));
    }

    @Transactional
    public LeadResponse update(UUID id, LeadRequest request) {
        Lead lead = requireLead(id);
        apply(lead, request);
        if (request.status() != null) {
            lead.setStatus(request.status());
        }
        return salesMapper.toResponse(leadRepository.save(lead));
    }

    @Transactional
    public void delete(UUID id) {
        leadRepository.delete(requireLead(id));
    }

    /** Converts a qualified lead into an open opportunity and marks the lead CONVERTED. */
    @Transactional
    public OpportunityResponse convert(UUID id) {
        Lead lead = requireLead(id);
        if (lead.getStatus() == LeadStatus.CONVERTED) {
            throw new ApiException(HttpStatus.CONFLICT, "Lead is already converted");
        }
        Opportunity opportunity = new Opportunity();
        opportunity.setOrganizationId(lead.getOrganizationId());
        opportunity.setName(lead.getCompany() != null ? lead.getCompany() : lead.getName());
        opportunity.setLeadId(lead.getId());
        opportunity.setOwnerUserId(lead.getAssignedUserId());
        opportunity.setStage(OpportunityStage.QUALIFICATION);
        opportunity.setValue(lead.getEstimatedValue());
        opportunity.setExpectedCloseDate(lead.getExpectedCloseDate());
        Opportunity saved = opportunityRepository.save(opportunity);

        lead.setStatus(LeadStatus.CONVERTED);
        leadRepository.save(lead);
        return salesMapper.toResponse(saved);
    }

    private Lead requireLead(UUID id) {
        return leadRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Lead", id));
    }

    private void apply(Lead lead, LeadRequest request) {
        lead.setName(request.name());
        lead.setCompany(request.company());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setSource(request.source());
        lead.setAssignedUserId(request.assignedUserId());
        lead.setEstimatedValue(request.estimatedValue());
        lead.setExpectedCloseDate(request.expectedCloseDate());
        lead.setNotes(request.notes());
    }
}
