package com.revenueos.sales;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    Optional<Lead> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query("""
            select l from Lead l
            where l.organizationId = :organizationId
              and (:status is null or l.status = :status)
            order by l.createdAt desc
            """)
    Page<Lead> search(@Param("organizationId") UUID organizationId,
                     @Param("status") LeadStatus status,
                     Pageable pageable);
}
