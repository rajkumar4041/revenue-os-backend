package com.revenueos.client;

import com.revenueos.client.dto.ClientResponse;
import com.revenueos.client.dto.CreateClientRequest;
import com.revenueos.client.dto.UpdateClientRequest;
import com.revenueos.common.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/clients")
@Tag(name = "Clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT_VIEW')")
    @Operation(operationId = "listClients", summary = "List clients in the current organization")
    public PageResponse<ClientResponse> list(
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return clientService.list(search, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_VIEW')")
    @Operation(operationId = "getClient", summary = "Get a client by id")
    public ClientResponse get(@PathVariable UUID id) {
        return clientService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @Operation(operationId = "createClient", summary = "Create a client")
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_UPDATE')")
    @Operation(operationId = "updateClient", summary = "Update a client")
    public ClientResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateClientRequest request) {
        return clientService.update(id, request);
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('CLIENT_DELETE')")
    @Operation(operationId = "archiveClient", summary = "Archive a client")
    public ClientResponse archive(@PathVariable UUID id) {
        return clientService.archive(id);
    }
}
