package com.ecom.product.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Product entity representing a product in the system.
 *
 * A product belongs to a category, optionally to a brand, and uses
 * a unit of measure. Each product is tenant-specific and uniquely
 * identified by its code within the tenant.
 */
@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
    },
    indexes = {
        @Index(name = "idx_product_tenant", columnList = "tenant_id"),
        @Index(name = "idx_product_code", columnList = "code"),
        @Index(name = "idx_product_brand", columnList = "brand_id"),
        @Index(name = "idx_product_category", columnList = "category_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    /**
     * Unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Tenant ID indicating which tenant this product belongs to.
     */
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    /**
     * Unique code for the product within the tenant.
     */
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    /**
     * Display name of the product.
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Optional description of the product.
     */
    @Column(name = "description", length = 2000)
    private String description;

    /**
     * Foreign Keys
     */

    /**
     * Category ID this product belongs to.
     */
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    /**
     * Optional brand ID for this product.
     */
    @Column(name = "brand_id")
    private UUID brandId;

    /**
     * Unit of measure ID for this product.
     */
    @Column(name = "unit_of_measure_id", nullable = false)
    private UUID unitOfMeasureId;

    /**
     * Price of the product.
     */
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Indicates if the product is active in the system.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Audit Fields
     */
    /**
     * Timestamp when the product was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the product was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relationships (READ-ONLY MAPPINGS)
     */
    /**
     * Category reference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Category category;

    /**
     * Brand reference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Brand brand;

    /**
     * Unit of measure reference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    
    @JoinColumn(name = "unit_of_measure_id", insertable = false, updatable = false)
    @ToString.Exclude
    private UnitOfMeasure unitOfMeasure;
}
