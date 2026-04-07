package com.ecom.product.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product.dto.request.ProductCreateRequest;
import com.ecom.product.dto.request.ProductUpdateRequest;
import com.ecom.product.dto.response.ProductResponse;
import com.ecom.product.entity.Brand;
import com.ecom.product.entity.Category;
import com.ecom.product.entity.Product;
import com.ecom.product.enums.ErrorCode;
import com.ecom.product.exception.BadRequestException;
import com.ecom.product.exception.ResourceNotFoundException;
import com.ecom.product.mapper.ProductMapper;
import com.ecom.product.repository.BrandRepository;
import com.ecom.product.repository.CategoryRepository;
import com.ecom.product.repository.ProductRepository;
import com.ecom.product.repository.UnitOfMeasureRepository;
import com.ecom.product.service.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of ProductService.
 *
 * Provides business logic for product management operations.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final ProductMapper productMapper;

    /**
     * Creates a new product for the specified tenant.
     *
     * @param request the product creation request
     * @param tenantId the tenant ID
     * @return the created product response
     * @throws BadRequestException if product code already exists or references are invalid
     */
    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        // Check if product code already exists
        if (productRepository.findByTenantIdAndCode(tenantUUID, request.getCode()).isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_PRODUCT,
                    "Product with code '" + request.getCode() + "' already exists");
        }

        // Validate all product references
        validateProductReferences(request.getCategoryId(), request.getBrandId(), 
                request.getUnitOfMeasureId(), tenantUUID);

        Product product = productMapper.mapToEntity(request, tenantUUID);
        Product savedProduct = productRepository.save(product);
        return productMapper.mapToResponse(savedProduct);
    }

    /**
     * Retrieves all products for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of product responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        return productRepository.findByTenantId(tenantUUID)
                .stream()
                .map(productMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by ID for the specified tenant.
     *
     * @param id the product ID
     * @param tenantId the tenant ID
     * @return the product response
     * @throws ResourceNotFoundException if product not found
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id, String tenantId) {
        Product product = findProduct(id); // Ensure product exists
        validateTenant(tenantId, product);
        return productMapper.mapToResponse(product);
    }

    /**
     * Retrieves all products in a category for the specified tenant.
     *
     * @param categoryId the category ID
     * @param tenantId the tenant ID
     * @return list of product responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(UUID categoryId, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .filter(product -> product.getTenantId().equals(tenantUUID))
                .map(productMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all active products for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of active product responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts(String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        return productRepository.findByTenantIdAndIsActive(tenantUUID, true)
                .stream()
                .map(productMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by code for the specified tenant.
     *
     * @param code the product code
     * @param tenantId the tenant ID
     * @return the product response
     * @throws ResourceNotFoundException if product not found
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByCode(String code, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        Product product = productRepository.findByTenantIdAndCode(tenantUUID, code)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with code: " + code));

        return productMapper.mapToResponse(product);
    }

    /**
     * Creates multiple products in bulk for the specified tenant.
     *
     * @param requests list of product creation requests
     * @param tenantId the tenant ID
     * @return list of created product responses
     */
    @Override
    @Transactional
    public List<ProductResponse> createBulkProducts(List<ProductCreateRequest> requests, String tenantId) {
        return requests.stream()
                .map(request -> createProduct(request, tenantId))
                .collect(Collectors.toList());
    }

    /**
     * Activates multiple products for the specified tenant.
     *
     * @param productIds list of product IDs to activate
     * @param tenantId the tenant ID
     * @return list of activated product responses
     */
    @Override
    @Transactional
    public List<ProductResponse> activateProducts(List<UUID> productIds, String tenantId) {
        return productIds.stream()
                .map(id -> {
                    Product product = findProduct(id); // Ensure product exists
                    validateTenant(tenantId, product);

                    product.setIsActive(true);
                    Product updatedProduct = productRepository.save(product);
                    return productMapper.mapToResponse(updatedProduct);
                })
                .collect(Collectors.toList());
    }

    /**
     * Deactivates multiple products for the specified tenant.
     *
     * @param productIds list of product IDs to deactivate
     * @param tenantId the tenant ID
     * @return list of deactivated product responses
     */
    @Override
    @Transactional
    public List<ProductResponse> deactivateProducts(List<UUID> productIds, String tenantId) {
        return productIds.stream()
                .map(id -> {
                    Product product = findProduct(id); // Ensure product exists
                    validateTenant(tenantId, product);

                    product.setIsActive(false);
                    Product updatedProduct = productRepository.save(product);
                    return productMapper.mapToResponse(updatedProduct);
                })
                .collect(Collectors.toList());
    }


    /**
     * Updates an existing product for the specified tenant.
     *
     * @param id the product ID
     * @param request the product update request
     * @param tenantId the tenant ID
     * @return the updated product response
     * @throws ResourceNotFoundException if product not found
     * @throws BadRequestException if references are invalid
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        Product product = findProduct(id); // Ensure product exists

        validateTenant(tenantId, product);

        // Validate all product references
        validateProductReferences(request.getCategoryId(), request.getBrandId(), 
                request.getUnitOfMeasureId(), tenantUUID);

        Product updatedProduct = productMapper.mapToEntity(request, product);
        Product savedProduct = productRepository.save(updatedProduct);
        return productMapper.mapToResponse(savedProduct);
    }
    
    /**
     * Deletes a product by ID for the specified tenant.
     *
     * @param id the product ID
     * @param tenantId the tenant ID
     * @throws ResourceNotFoundException if product not found
     */
    @Override
    @Transactional
    public void deleteProduct(UUID id, String tenantId) {
        Product product = findProduct(id); // Ensure product exists
        validateTenant(tenantId, product);
        productRepository.delete(product);
    }

    /**
     * Helper method to find a product by ID and throw ResourceNotFoundException if not found.
     * 
     * @param id the product ID
     * @return the found product
     * @throws ResourceNotFoundException if product not found
     */
    private Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with id: " + id));
    }
    
    /**
     * The fetched brand actually belongs to the requesting tenant
     * Prevents cross-tenant data access
     * Enforces tenant isolation
     * 
     * @tenantId the tenant ID from request header
     * @product the product entity fetched from database
     */
    private void validateTenant(String tenantId, Product product) {
        UUID tenantUUID = UUID.fromString(tenantId);
        if (!product.getTenantId().equals(tenantUUID)) {
            throw new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND,
                    "Product with id '" + product.getId() + "' does not belong to the current tenant");
        }
    }

    /**
     * Validates product references (category, brand, and unit of measure).
     *
     * @param categoryId the category ID to validate
     * @param brandId the brand ID to validate (can be null)
     * @param unitOfMeasureId the unit of measure ID to validate
     * @param tenantUUID the tenant UUID for validation
     * @throws BadRequestException if validation fails
     */
    private void validateProductReferences(UUID categoryId, UUID brandId, UUID unitOfMeasureId, UUID tenantUUID) {
        validateCategory(categoryId, tenantUUID);
        validateBrand(brandId, tenantUUID);
        validateUnitOfMeasure(unitOfMeasureId);
    }

    /**
     * Validates that the category exists and belongs to the tenant.
     *
     * @param categoryId the category ID to validate
     * @param tenantUUID the tenant UUID
     * @throws BadRequestException if category not found or doesn't belong to tenant
     */
    private void validateCategory(UUID categoryId, UUID tenantUUID) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_CATEGORY_ID,
                        "Category not found with id: " + categoryId));

        if (!category.getTenantId().equals(tenantUUID)) {
            throw new BadRequestException(ErrorCode.INVALID_CATEGORY_ID,
                    "Category does not belong to the current tenant");
        }
    }

    /**
     * Validates that the brand exists and belongs to the tenant (if provided).
     *
     * @param brandId the brand ID to validate (can be null)
     * @param tenantUUID the tenant UUID
     * @throws BadRequestException if brand doesn't belong to tenant
     */
    private void validateBrand(UUID brandId, UUID tenantUUID) {
        if (brandId == null) {
            return; // Brand is optional
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_BRAND_ID,
                        "Brand not found with id: " + brandId));
        
        if (!brand.getTenantId().equals(tenantUUID)) {
            throw new BadRequestException(ErrorCode.INVALID_BRAND_ID,
                    "Brand does not belong to the current tenant");
        }
    }

    /**
     * Validates that the unit of measure exists.
     *
     * @param unitOfMeasureId the unit of measure ID to validate
     * @throws BadRequestException if unit of measure not found
     */
    private void validateUnitOfMeasure(UUID unitOfMeasureId) {
        unitOfMeasureRepository.findById(unitOfMeasureId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_UNIT_OF_MEASURE_ID,
                        "Unit of measure not found with id: " + unitOfMeasureId));
    }

}
