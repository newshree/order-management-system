package com.ecom.product.repository;

import com.ecom.product.entity.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UnitOfMeasure entity.
 *
 * Provides database operations for UnitOfMeasure entities.
 */
@Repository
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, UUID> {

    /**
     * Finds a unit of measure by code.
     *
     * @param code the unit of measure code
     * @return Optional containing the unit of measure if found
     */
    Optional<UnitOfMeasure> findByCode(String code);
}
