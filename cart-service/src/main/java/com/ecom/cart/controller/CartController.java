package com.ecom.cart.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.cart.dto.request.AddItemToCartRequest;
import com.ecom.cart.dto.request.UpdateCartItemQuantityRequest;
import com.ecom.cart.dto.response.ApiResponse;
import com.ecom.cart.dto.response.CartItemResponse;
import com.ecom.cart.dto.response.CartResponse;
import com.ecom.cart.dto.response.CartValidationResponse;
import com.ecom.cart.dto.response.CheckoutSummaryResponse;
import com.ecom.cart.exception.ResourceNotFoundException;
import com.ecom.cart.service.CartService;

import jakarta.validation.Valid;

/**
 * CartController - REST API endpoints for cart management.
 * 
 * Defines all HTTP endpoints for cart operations including:
 * - Cart retrieval and management
 * - Item management (add, update, remove)
 * - Cart validation and checkout summary
 * - Cart synchronization with product service
 *    
 */
@CrossOrigin(origins = "http://localhost:3000")  // TODO: Change for production
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

//     private static final String MOCK_USER_ID = "550e8400-e29b-41d4-a716-446655440000";  // TODO: Remove mock user ID
    
    /**
     * Gets the complete shopping cart for the authenticated user.
     * Creates a new cart if one doesn't exist.
     * 
     * @param userId the UUID of the user (passed via header or context)
     * @return ResponseEntity with CartResponse containing cart details
     */
    @GetMapping("/getCart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
        @RequestHeader("X-User-Id") UUID userId)
    {
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .success(true)
                .data(response)
                .message("Cart retrieved successfully")
                .build());
    }

    /**
     * Adds a product to the user's cart with specified quantity.
     * If product already exists in cart, increases quantity.
     *
     * @param request AddItemToCartRequest containing product ID and quantity (validated)
     * @param userId the UUID of the user
     * @return ResponseEntity with CartResponse containing updated cart
     * @throws ResourceNotFoundException if product not found
     * @throws BadRequestException if quantity is invalid
     */
    @PostMapping("/addItem")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @Valid @RequestBody AddItemToCartRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        CartResponse response = cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CartResponse>builder()
                .success(true)
                .data(response)
                .message("Item added to cart successfully")
                .build());
    }

    /**
     * PUT /api/cart/items/{itemId} - Update item quantity.
     * 
     * Updates the quantity of a specific item in the cart.
     * 
     * Request Body Example:
     * {
     *   "quantity": 5
     * }
     * 
     * @param itemId the UUID of the cart item to update
     * @param request UpdateCartItemQuantityRequest containing new quantity (validated)
     * @param userId the UUID of the user
     * @return ResponseEntity with CartResponse containing updated cart
     * @throws ResourceNotFoundException if cart or item not found
     * @throws BadRequestException if quantity is invalid (0 or negative)
     */
    @PutMapping("/updateItemQuantity/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        CartResponse response = cartService.updateItemQuantity(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .success(true)
                .data(response)
                .message("Item quantity updated successfully")
                .build());
    }

    /**
     * DELETE /api/cart/items/{itemId} - Remove item from cart.
     * 
     * Removes a specific item from the user's cart.
     * 
     * @param itemId the UUID of the cart item to remove
     * @param userId the UUID of the user
     * @return ResponseEntity with CartResponse containing updated cart
     * @throws ResourceNotFoundException if cart or item not found
     */
    @DeleteMapping("/removeItem/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(
            @PathVariable UUID itemId,
            @RequestHeader("X-User-Id") UUID userId) {
        CartResponse response = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .success(true)
                .data(response)
                .message("Item removed from cart successfully")
                .build());
    }

    /**
     * DELETE /api/cart - Clear cart.
     * 
     * Removes all items from the user's cart.
     * 
     * @param userId the UUID of the user
     * @return ResponseEntity with empty CartResponse
     * @throws ResourceNotFoundException if cart not found
     */
    @DeleteMapping("/clearCart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(
            @RequestHeader("X-User-Id") UUID userId) {
        CartResponse response = cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .success(true)
                .data(response)
                .message("Cart cleared successfully")
                .build());
    }

    /**
     * POST /api/cart/validate - Validate cart for checkout.
     * 
     * Validates all cart items for checkout readiness.
     * Checks product availability, pricing, and inventory.
     * 
     * Response Example:
     * {
     *   "isValid": true,
     *   "errorCount": 0,
     *   "warningCount": 1,
     *   "validationErrors": [],
     *   "validationWarnings": ["Item X price has increased"]
     * }
     * 
     * @param userId the UUID of the user
     * @return ResponseEntity with CartValidationResponse containing validation results
     * @throws ResourceNotFoundException if cart not found
     */
    @PostMapping("/validateCart")
    public ResponseEntity<ApiResponse<CartValidationResponse>> validateCart(
            @RequestHeader("X-User-Id") UUID userId) {
        CartValidationResponse response = cartService.validateCart(userId);
        return ResponseEntity.ok(ApiResponse.<CartValidationResponse>builder()
                .success(true)
                .data(response)
                .message("Cart validation completed")
                .build());
    }

    /**
     * GET /api/cart/checkout - Get checkout summary.
     * 
     * Provides complete pricing breakdown for checkout:
     * - Subtotal (sum of all items)
     * - Tax (calculated as 10% of subtotal)
     * - Delivery charge (fixed amount)
     * - Final total
     * 
     * Response Example:
     * {
     *   "items": [...],
     *   "subtotal": 1999.98,
     *   "tax": 199.99,
     *   "deliveryCharge": 50.00,
     *   "totalAmount": 2249.97,
     *   "finalAmount": 2249.97
     * }
     * 
     * @param userId the UUID of the user
     * @return ResponseEntity with CheckoutSummaryResponse containing pricing details
     * @throws ResourceNotFoundException if cart not found or empty
     * @throws BadRequestException if cart is empty
     */
    @GetMapping("/getCheckoutSummary")
    public ResponseEntity<ApiResponse<CheckoutSummaryResponse>> getCheckoutSummary(
            @RequestHeader("X-User-Id") UUID userId) {
        CheckoutSummaryResponse response = cartService.getCheckoutSummary(userId);
        return ResponseEntity.ok(ApiResponse.<CheckoutSummaryResponse>builder()
                .success(true)
                .data(response)
                .message("Checkout summary retrieved successfully")
                .build());
    }

    /**
     * POST /api/cart/sync - Sync cart with product service.
     * 
     * Synchronizes cart with latest product information:
     * - Updates prices from Product Service
     * - Removes unavailable items
     * - Recalculates totals
     * 
     * @param userId the UUID of the user
     * @return ResponseEntity with CartResponse containing synced cart
     * @throws ResourceNotFoundException if cart not found
     */
    @PostMapping("/syncCart")
    public ResponseEntity<ApiResponse<CartResponse>> syncCart(
            @RequestHeader("X-User-Id") UUID userId) {
        CartResponse response = cartService.syncCart(userId);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .success(true)
                .data(response)
                .message("Cart synced successfully")
                .build());
    }

    /**
     * 
     * Retrieves details of a specific item in the cart.
     * 
     * @param itemId the UUID of the cart item
     * @param userId the UUID of the user
     * @return ResponseEntity with CartItemResponse containing item details
     * @throws ResourceNotFoundException if cart or item not found
     */
    @GetMapping("/getCartItems/{itemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> getCartItem(
            @PathVariable UUID itemId,
            @RequestHeader("X-User-Id") UUID userId) {
        CartItemResponse response = cartService.getCartItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.<CartItemResponse>builder()
                .success(true)
                .data(response)
                .message("Cart item retrieved successfully")
                .build());
    }
}
