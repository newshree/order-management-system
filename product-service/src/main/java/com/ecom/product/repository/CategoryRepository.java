package com.ecom.product.repository;

import com.ecom.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Category entity.
 *
 * Provides database operations for Category entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Finds a category by tenant ID and code.
     *
     * @param tenantId the tenant ID
     * @param code the category code
     * @return Optional containing the category if found
     */
    Optional<Category> findByTenantIdAndCode(UUID tenantId, String code);

    /**
     * Finds all categories for a specific tenant.
     *
     * @param tenantId the tenant ID
     * @return list of categories for the tenant
     */
    List<Category> findByTenantId(UUID tenantId);

    /**
     * Finds all sub-categories for a parent category.
     *
     * @param parentCategoryId the parent category ID
     * @return list of sub-categories
     */
    List<Category> findByParentCategoryId(UUID parentCategoryId);
}
