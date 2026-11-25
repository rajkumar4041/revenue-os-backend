package com.revenueos.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {

    Optional<Opportunity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    List<Opportunity> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
}
