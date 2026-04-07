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
 * UnitOfMeasure entity representing measurement units for products.
 *
 * Units of measure define how products are measured (e.g., kg, liter, piece).
 * This is a tenant-independent entity shared across all tenants.
 */
@Entity
@Table(
    name = "units_of_measure",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
    },
    indexes = {
        @Index(name = "idx_uom_code", columnList = "code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UnitOfMeasure {

    /**
     * Unique identifier for the unit of measure.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Unique code for the unit of measure.
     */
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    /**
     * Display name of the unit of measure.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Timestamp when the unit of measure was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the unit of measure was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Read-only relationship
     */
    /**
     * Collection of products using this unit of measure.
     */
    @OneToMany(mappedBy = "unitOfMeasure", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
