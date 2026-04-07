package com.ecom.product.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product.dto.request.ProductCreateRequest;
import com.ecom.product.dto.request.ProductUpdateRequest;
import com.ecom.product.dto.response.ApiResponse;
import com.ecom.product.dto.response.ProductResponse;
import com.ecom.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Product management.
 *
 * Provides endpoints for CRUD operations on products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private static final String TENANT_ID = "2f34e34a-a524-4aff-9702-35e6f8c6f9d1";

    /**
     * Creates a new product.
     *
     * @param request the product creation request
     * @param tenantId the tenant ID from request header
     * @return API response with the created product
     */
    @PostMapping("/createProduct")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        ProductResponse product = productService.createProduct(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProductResponse>builder()
                .success(true)
                .data(product)
                .message("Product created successfully")
                .build());
    }

    /**
     * Retrieves all products.
     *
     * @param tenantId the tenant ID from request header
     * @return API response with list of products
     */
    @GetMapping("/getAllProducts")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<ProductResponse> products = productService.getAllProducts(tenantId);
        return ResponseEntity.ok(ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .data(products)
                .message("Products retrieved successfully")
                .build());
    }

    /**
     * Retrieves a product by ID.
     *
     * @param id the product ID
     * @param tenantId the tenant ID from request header
     * @return API response with the product
     */
    @GetMapping("/getProductById/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        ProductResponse product = productService.getProductById(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .success(true)
                .data(product)
                .message("Product retrieved successfully")
                .build());
    }

    /**
     * Retrieves all products in a specific category.
     *
     * @param categoryId the category ID
     * @param tenantId the tenant ID from request header
     * @return API response with list of products
     */
    @GetMapping("/getProductsByCategory/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId, tenantId);
        return ResponseEntity.ok(ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .data(products)
                .message("Products retrieved successfully")
                .build());
    }

    /**
     * Retrieves all active products.
     *
     * @param tenantId the tenant ID from request header
     * @return API response with list of active products
     */
    @GetMapping("/getActiveProducts")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<ProductResponse> products = productService.getActiveProducts(tenantId);
        return ResponseEntity.ok(ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .data(products)
                .message("Active products retrieved successfully")
                .build());
    }

    /**
     * Retrieves a product by code.
     *
     * @param code the product code
     * @param tenantId the tenant ID from request header
     * @return API response with the product
     */
    @GetMapping("/getProductByCode/{code}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByCode(
            @PathVariable String code,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        ProductResponse product = productService.getProductByCode(code, tenantId);
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .success(true)
                .data(product)
                .message("Product retrieved successfully")
                .build());
    }

    /**
     * Creates multiple products in bulk.
     *
     * @param requests list of product creation requests
     * @param tenantId the tenant ID from request header
     * @return API response with list of created products
     */
    @PostMapping("/createBulkProducts")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> createBulkProducts(
            @Valid @RequestBody List<ProductCreateRequest> requests,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<ProductResponse> products = productService.createBulkProducts(requests, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .data(products)
                .message("Products created successfully")
                .build());
    }

    /**
     * Activates multiple products.
     *
     * @param productIds list of product IDs to activate
     * @param tenantId the tenant ID from request header
     * @return API response with list of activated products
     */
    @PutMapping("/activate")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> activateProducts(
            @RequestBody List<UUID> productIds,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<ProductResponse> products = productService.activateProducts(productIds, tenantId);
        return ResponseEntity.ok(ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .data(products)
                .message("Products activated successfully")
                .build());
    }

    /**
     * Deactivates multiple products.
     *
     * @param productIds list of product IDs to deactivate
     * @param tenantId the tenant ID from request header
     * @return API response with list of deactivated products
     */
    @PutMapping("/deactivate")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> deactivateProducts(
            @RequestBody List<UUID> productIds,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<ProductResponse> products = productService.deactivateProducts(productIds, tenantId);
        return ResponseEntity.ok(ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .data(products)
                .message("Products deactivated successfully")
                .build());
    }

    /**
     * Updates an existing product.
     *
     * @param id the product ID
     * @param request the product update request
     * @param tenantId the tenant ID from request header
     * @return API response with the updated product
     */
    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        ProductResponse product = productService.updateProduct(id, request, tenantId);
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .success(true)
                .data(product)
                .message("Product updated successfully")
                .build());
    }
    
    /**
     * Deletes a product.
     *
     * @param id the product ID
     * @param tenantId the tenant ID from request header
     * @return API response indicating successful deletion
     */
    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        productService.deleteProduct(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Product deleted successfully")
                .build());
    }
}
