package com.revenueos.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByOrganizationIdOrderByName(UUID organizationId);

    Optional<Role> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<Role> findByOrganizationIdAndName(UUID organizationId, String name);

    boolean existsByOrganizationIdAndName(UUID organizationId, String name);

    Set<Role> findByOrganizationIdAndIdIn(UUID organizationId, Collection<UUID> ids);
}
