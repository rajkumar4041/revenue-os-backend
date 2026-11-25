package com.revenueos.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByInvoiceIdOrderByPaidOnAsc(UUID invoiceId);

    List<Payment> findByOrganizationId(UUID organizationId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.invoiceId = :invoiceId")
    BigDecimal sumForInvoice(@Param("invoiceId") UUID invoiceId);
}
