package com.ecom.product.repository;

import com.ecom.product.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Brand entity.
 *
 * Provides database operations for Brand entities.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    /**
     * Finds a brand by tenant ID and code.
     *
     * @param tenantId the tenant ID
     * @param code the brand code
     * @return Optional containing the brand if found
     */
    Optional<Brand> findByTenantIdAndCode(UUID tenantId, String code);

    /**
     * Finds all brands for a specific tenant.
     *
     * @param tenantId the tenant ID
     * @return list of brands for the tenant
     */
    List<Brand> findByTenantId(UUID tenantId);
}
