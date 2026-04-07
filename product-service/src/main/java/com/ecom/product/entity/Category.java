package com.ecom.product.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
 * Category entity representing a product category in the system.
 *
 * Categories are hierarchical, allowing for parent-child relationships.
 * Each category is tenant-specific and contains a unique code and name.
 */
@Entity
@Table(
    name = "categories",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "code"})
    },
    indexes = {
        @Index(name = "idx_category_tenant", columnList = "tenant_id"),
        @Index(name = "idx_category_code", columnList = "code"),
        @Index(name = "idx_category_parent", columnList = "parent_category_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Tenant ID indicating which tenant this category belongs to.
     */
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    /**
     * Unique code for the category within the tenant.
     */
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    /**
     * Display name of the category.
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Parent category ID for hierarchical categorization.
     */
    @Column(name = "parent_category_id")
    private UUID parentCategoryId;

    /**
     * Optional description of the category.
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Timestamp when the category was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the category was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Read-only Relationships
     */
    /**
     * Parent category reference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Category parentCategory;

    /**
     * Collection of sub-categories.
     */
    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Category> subCategories = new ArrayList<>();

    /**
     * Collection of products in this category.
     */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
