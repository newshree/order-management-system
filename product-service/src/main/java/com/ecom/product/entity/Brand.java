package com.ecom.product.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
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
 * Brand entity representing a product brand in the system.
 *
 * A brand is an optional entity that can be associated with products.
 * Each brand is tenant-specific and contains a unique code and name.
 */
@Entity
@Table(
    name = "brands",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
    },
    indexes = {
        @Index(name = "idx_brand_tenant", columnList = "tenant_id"),
        @Index(name = "idx_brand_code", columnList = "code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Brand {

    /**
     * Unique identifier for the brand.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private UUID id;

    /**
     * Tenant ID indicating which tenant this brand belongs to.
     */
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    /**
     * Unique code for the brand within the tenant.
     */
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    /**
     * Display name of the brand.
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Optional description of the brand.
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Timestamp when the brand was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the brand was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Collection of products associated with this brand.
     */
    @OneToMany(
        mappedBy = "brand",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();
}
