package com.revenueos.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByIdAndOrganizationId(UUID id, UUID organizationId);

    long countByOrganizationId(UUID organizationId);

    java.util.List<Invoice> findByOrganizationId(UUID organizationId);

    @Query("""
            select i from Invoice i
            where i.organizationId = :organizationId
              and (:clientId is null or i.clientId = :clientId)
              and (:status is null or i.status = :status)
            order by i.issueDate desc
            """)
    Page<Invoice> search(@Param("organizationId") UUID organizationId,
                        @Param("clientId") UUID clientId,
                        @Param("status") InvoiceStatus status,
                        Pageable pageable);
}
