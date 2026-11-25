package com.revenueos.client;

import com.revenueos.client.dto.ClientResponse;
import com.revenueos.client.dto.CreateClientRequest;
import com.revenueos.client.dto.UpdateClientRequest;
import com.revenueos.audit.AuditService;
import com.revenueos.common.exception.ResourceNotFoundException;
import com.revenueos.common.web.PageResponse;
import com.revenueos.tenant.TenantContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AuditService auditService;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, AuditService auditService) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public PageResponse<ClientResponse> list(String search, Pageable pageable) {
        String term = (search == null || search.isBlank()) ? null : search.trim();
        return PageResponse.of(
                clientRepository.search(TenantContext.getOrganizationId(), term, pageable),
                clientMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClientResponse get(UUID id) {
        return clientMapper.toResponse(requireClient(id));
    }

    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        Client client = new Client();
        client.setOrganizationId(TenantContext.getOrganizationId());
        client.setName(request.name());
        client.setCompanyName(request.companyName());
        client.setIndustry(request.industry());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());
        client.setCountry(request.country());
        client.setWebsite(request.website());
        if (request.clientType() != null) {
            client.setClientType(request.clientType());
        }
        if (request.status() != null) {
            client.setStatus(request.status());
        }
        client.setAccountManagerUserId(request.accountManagerUserId());
        client.setContractStartDate(request.contractStartDate());
        client.setContractEndDate(request.contractEndDate());
        client.setContractValue(request.contractValue());
        client.setPaymentTerms(request.paymentTerms());
        client.setNotes(request.notes());
        Client saved = clientRepository.save(client);
        auditService.record("CLIENT_CREATED", "Client", saved.getId(), "Added client " + saved.getName());
        return clientMapper.toResponse(saved);
    }

    @Transactional
    public ClientResponse update(UUID id, UpdateClientRequest request) {
        Client client = requireClient(id);
        client.setName(request.name());
        client.setCompanyName(request.companyName());
        client.setIndustry(request.industry());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());
        client.setCountry(request.country());
        client.setWebsite(request.website());
        if (request.clientType() != null) {
            client.setClientType(request.clientType());
        }
        if (request.status() != null) {
            client.setStatus(request.status());
        }
        client.setAccountManagerUserId(request.accountManagerUserId());
        client.setContractStartDate(request.contractStartDate());
        client.setContractEndDate(request.contractEndDate());
        client.setContractValue(request.contractValue());
        client.setPaymentTerms(request.paymentTerms());
        client.setNotes(request.notes());
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponse archive(UUID id) {
        Client client = requireClient(id);
        client.setArchived(true);
        client.setStatus(ClientStatus.ARCHIVED);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    private Client requireClient(UUID id) {
        return clientRepository.findByIdAndOrganizationId(id, TenantContext.getOrganizationId())
                .orElseThrow(() -> ResourceNotFoundException.of("Client", id));
    }
}
