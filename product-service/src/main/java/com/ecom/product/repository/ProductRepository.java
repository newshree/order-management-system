package com.ecom.product.repository;

import com.ecom.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Product entity.
 *
 * Provides database operations for Product entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Finds a product by tenant ID and code.
     *
     * @param tenantId the tenant ID
     * @param code the product code
     * @return Optional containing the product if found
     */
    Optional<Product> findByTenantIdAndCode(UUID tenantId, String code);

    /**
     * Finds all products for a specific tenant.
     *
     * @param tenantId the tenant ID
     * @return list of products for the tenant
     */
    List<Product> findByTenantId(UUID tenantId);

    /**
     * Finds all products in a specific category.
     *
     * @param categoryId the category ID
     * @return list of products in the category
     */
    List<Product> findByCategoryId(UUID categoryId);

    /**
     * Finds all products from a specific brand.
     *
     * @param brandId the brand ID
     * @return list of products from the brand
     */
    List<Product> findByBrandId(UUID brandId);

    /**
     * Finds all active products for a tenant.
     *
     * @param tenantId the tenant ID
     * @param isActive the active status
     * @return list of active/inactive products
     */
    List<Product> findByTenantIdAndIsActive(UUID tenantId, Boolean isActive);
}
