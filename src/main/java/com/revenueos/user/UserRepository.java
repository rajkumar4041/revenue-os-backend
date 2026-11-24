package com.revenueos.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
            select distinct u from User u
            left join fetch u.roles r
            left join fetch r.permissions
            where u.email = :email
            """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("""
            select distinct u from User u
            left join fetch u.roles r
            left join fetch r.permissions
            where u.id = :id
            """)
    Optional<User> findByIdWithRoles(@Param("id") UUID id);

    Page<User> findByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<User> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
