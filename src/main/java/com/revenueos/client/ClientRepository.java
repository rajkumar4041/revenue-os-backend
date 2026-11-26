package com.revenueos.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByIdAndOrganizationId(UUID id, UUID organizationId);

    // An empty search term matches everything (like '%%'); callers pass "" rather
    // than null so Postgres can always infer the parameter type inside concat().
    @Query("""
            select c from Client c
            where c.organizationId = :organizationId
              and (lower(c.name) like lower(concat('%', :search, '%'))
                   or (c.companyName is not null
                       and lower(c.companyName) like lower(concat('%', :search, '%'))))
            """)
    Page<Client> search(@Param("organizationId") UUID organizationId,
                        @Param("search") String search,
                        Pageable pageable);
}
