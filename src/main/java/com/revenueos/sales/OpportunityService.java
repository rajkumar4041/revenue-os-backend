package com.revenueos.sales;

import com.revenueos.client.Client;
import com.revenueos.client.ClientRepository;
import com.revenueos.client.ClientStatus;
import com.revenueos.client.ClientType;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.sales.dto.OpportunityRequest;
import com.revenueos.sales.dto.OpportunityResponse;
import com.revenueos.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final ClientRepository clientRepository;
    private final SalesMapper salesMapper;

    public OpportunityService(OpportunityRepository opportunityRepository, ClientRepository clientRepository,
                              SalesMapper salesMapper) {
        this.opportunityRepository = opportunityRepository;
        this.clientRepository = clientRepository;
        this.salesMapper = salesMapper;
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> list() {
        return opportunityRepository.findByOrganizationIdOrderByCreatedAtDesc(TenantContext.getOrganizationId())
                .stream().map(salesMapper::toResponse).toList();
    }

    @Transactional
    public OpportunityResponse create(OpportunityRequest request) {
        Opportunity opportunity = new Opportunity();
        opportunity.setOrganizationId(TenantContext.getOrganizationId());
        apply(opportunity, request);
        if (request.stage() != null) {
            opportunity.setStage(request.stage());
        }
        return salesMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponse update(UUID id, OpportunityRequest request) {
        Opportunity opportunity = requireOpportunity(id);
        apply(opportunity, request);
        if (request.stage() != null) {
            opportunity.setStage(request.stage());
        }
        return salesMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponse setStage(UUID id, OpportunityStage stage) {
        Opportunity opportunity = requireOpportunity(id);
        opportunity.setStage(stage);
        return salesMapper.toResponse(opportunityRepository.save(opportunity));
    }

    /** Marks the deal WON and, if it isn't linked to a client yet, creates one (handover). */
    @Transactional
    public OpportunityResponse win(UUID id) {
        Opportunity opportunity = requireOpportunity(id);
        opportunity.setStage(OpportunityStage.WON);
        if (opportunity.getClientId() == null) {
            Client client = new Client();
            client.setOrganizationId(opportunity.getOrganizationId());
            client.setName(opportunity.getName());
            client.setClientType(ClientType.DIRECT);
            client.setStatus(ClientStatus.ACTIVE);
            client.setContractValue(opportunity.getValue());
            Client saved = clientRepository.save(client);
            opportunity.setClientId(saved.getId());
        }
        return salesMapper.toResponse(opportunityRepository.save(opportunity));
    }

    private Opportunity requireOpportunity(UUID id) {
        return opportunityRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Opportunity", id));
    }

    private void apply(Opportunity opportunity, OpportunityRequest request) {
        opportunity.setName(request.name());
        opportunity.setClientId(request.clientId());
        opportunity.setOwnerUserId(request.ownerUserId());
        opportunity.setValue(request.value());
        opportunity.setExpectedCloseDate(request.expectedCloseDate());
        opportunity.setProbability(request.probability());
        opportunity.setNotes(request.notes());
    }
}
